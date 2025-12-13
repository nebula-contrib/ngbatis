package org.nebula.contrib.ngbatis.session;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.nebula.contrib.ngbatis.proxy.MapperProxy.ENV;
import static org.springframework.util.ObjectUtils.nullSafeEquals;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.SessionPool;
import com.vesoft.nebula.client.graph.SessionPoolConfig;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.BindSpaceFailedException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.net.Session;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.nebula.contrib.ngbatis.SessionDispatcher;
import org.nebula.contrib.ngbatis.config.EnvConfig;
import org.nebula.contrib.ngbatis.config.NebulaJdbcProperties;
import org.nebula.contrib.ngbatis.config.NgbatisConfig;
import org.nebula.contrib.ngbatis.exception.QueryException;
import org.nebula.contrib.ngbatis.models.MapperContext;
import org.nebula.contrib.ngbatis.utils.ResultSetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 间隔时间进行检查的本地会话调度器。
 * 
 * @author yeweicheng
 * @since 2022-08-26 2:34
 * <br>Now is history!
 */
@Component
public class IntervalCheckSessionDispatcher implements Runnable, SessionDispatcher {

  public static long SESSION_LIFE_LENGTH = 5 * 60 * 60 * 1000;
  public static long CHECK_FIXED_RATE = 5 * 60 * 60 * 1000; // unit ms
  private static Logger log = LoggerFactory.getLogger(IntervalCheckSessionDispatcher.class);
  private final NebulaPoolConfig nebulaPoolConfig;
  private final ArrayBlockingQueue<LocalSession> sessionQueue;
  private final ScheduledExecutorService threadPool;
  private final NebulaJdbcProperties nebulaJdbcProperties;

  /**
   * 具备间隔时间做连接可用性检查的会话调度器
   * @param properties 连接信息
   */
  public IntervalCheckSessionDispatcher(NebulaJdbcProperties properties) {
    this.nebulaJdbcProperties = properties;
    this.nebulaPoolConfig = properties.getPoolConfig();
    this.sessionQueue = new ArrayBlockingQueue<>(nebulaPoolConfig.getMaxConnSize());
    threadPool = EnvConfig.reconnect ? Executors.newScheduledThreadPool(1) : null;
    //使用自定义的 session存活有效期/健康检测间隔
    NgbatisConfig ngbatis = properties.getNgbatis();
    if (ngbatis != null) {
      SESSION_LIFE_LENGTH = ngbatis.getSessionLifeLength() == null
        ? SESSION_LIFE_LENGTH : ngbatis.getSessionLifeLength();
      CHECK_FIXED_RATE = ngbatis.getCheckFixedRate() == null
        ? CHECK_FIXED_RATE : ngbatis.getCheckFixedRate();
    }

    setNebulaSessionPool(MapperContext.newInstance());
//    wakeUp();
  }

  @EventListener(ContextRefreshedEvent.class)
  public void onAppReady() {
    // 整个容器已就绪
    wakeUp();
  }

  @Override
  public synchronized void run() {
    for (LocalSession session : sessionQueue) {
      log.info(
          "LocalSession in queue which created at {}, useCount: {}",
          session.getBirth(), session.useCount
      );

      boolean finished = timeToRelease(session);
      if (finished || !session.getSession().ping()) {
        release(session);
      }
    }
    while (sessionQueue.size() < nebulaPoolConfig.getMinConnSize()) {
      offer();
    }
  }

  @Override
  public void offer(LocalSession session) {
    boolean offer = sessionQueue.offer(session);
    if (!offer) {
      releaseInnerSession(session);
    }
  }

  private void offer() {
    LocalSession localSession = newLocalSession();
    offer(localSession);
  }

  @Override
  public synchronized LocalSession poll() {
    LocalSession localSession = null;
    if (!EnvConfig.reconnect) {
      localSession = newLocalSession();
      localSession.useCount++;
      return localSession;
    }
    try {
      localSession = sessionQueue.poll(nebulaPoolConfig.getWaitTime(), TimeUnit.MILLISECONDS);
      localSession = localSession == null ? newLocalSession() : localSession;
      localSession.useCount++;
      return localSession;
    } catch (InterruptedException e) {
      return newLocalSession();
    }
  }

  private void wakeUp() {
    if (threadPool != null) {
      threadPool.scheduleAtFixedRate(this, 3L, CHECK_FIXED_RATE, TimeUnit.MILLISECONDS);
    }
  }

  private LocalSession newLocalSession() {
    return new LocalSession(System.currentTimeMillis(), ENV.openSession());
  }

  private void releaseInnerSession(LocalSession session) {
    session.getSession().release();
  }

  @Override
  public void release(LocalSession session) {
    session.getSession().release();
    log.info("Release a session which created at {}", session.getBirth());
    sessionQueue.remove(session);
  }

  @Override
  public void releaseAll() {
    while (sessionQueue.size() > 0) {
      LocalSession poll = sessionQueue.poll();
      release(poll);
    }
    if (SessionDispatcher.useSessionPool()) {
      MapperContext.newInstance()
        .getNebulaSessionPoolMap()
        .forEach((k, v) -> v.close());
    }
  }

  @Override
  public boolean timeToRelease(LocalSession session) {
    long birth = session.getBirth();
    return System.currentTimeMillis() - birth > SESSION_LIFE_LENGTH;
  }

  /**
   * create and init Nebula SessionPool
   * 
   * @author gin soul [create] 
   * @author CorvusYe [refac]
   */
  @Override
  public void setNebulaSessionPool(MapperContext context) {
    NgbatisConfig ngbatisConfig = nebulaJdbcProperties.getNgbatis();
    if (ngbatisConfig.getUseSessionPool() == null || !ngbatisConfig.getUseSessionPool()) {
      return;
    }

    context.getSpaceNameSet().add(nebulaJdbcProperties.getSpace());
    Map<String, SessionPool> nebulaSessionPoolMap = context.getNebulaSessionPoolMap();
    for (String spaceName : context.getSpaceNameSet()) {
      SessionPool sessionPool = initSessionPool(spaceName);
      if (sessionPool == null) {
        log.error("{} session pool init failed.", spaceName);
        continue;
      }
      log.info("session pool for `{}` init success.", spaceName);
      nebulaSessionPoolMap.put(spaceName, sessionPool);
    }
  }

  /**
   * session pool create and init
   * @param spaceName nebula
  space name
   * @author gin soul [create]
   * @return inited SessionPool
   */
  @Override
  public SessionPool initSessionPool(String spaceName) {
    final NgbatisConfig ngbatisConfig = nebulaJdbcProperties.getNgbatis();
    NebulaPoolConfig poolConfig = nebulaJdbcProperties.getPoolConfig();

    SessionPoolConfig sessionPoolConfig = new SessionPoolConfig(
      nebulaJdbcProperties.getHostAddresses(),
      spaceName,
      nebulaJdbcProperties.getUsername(),
      nebulaJdbcProperties.getPassword()
    ).setUseHttp2(poolConfig.isUseHttp2())
      .setEnableSsl(poolConfig.isEnableSsl())
      .setSslParam(poolConfig.getSslParam())
      .setCustomHeaders(poolConfig.getCustomHeaders())
      .setWaitTime(poolConfig.getWaitTime())
      .setTimeout(poolConfig.getTimeout());

    if (poolConfig.getMinConnSize() <= 0) {
      sessionPoolConfig.setMinSessionSize(1);
    } else {
      sessionPoolConfig.setMinSessionSize(poolConfig.getMinConnSize());
    }
    sessionPoolConfig.setMaxSessionSize(poolConfig.getMaxConnSize());
    sessionPoolConfig.setTimeout(poolConfig.getTimeout());
    sessionPoolConfig.setWaitTime(poolConfig.getWaitTime());
    if (null != ngbatisConfig.getSessionLifeLength()) {
      int cleanTime = (int) (ngbatisConfig.getSessionLifeLength() / 1000);
      sessionPoolConfig.setCleanTime(cleanTime);
    }
    if (null != ngbatisConfig.getCheckFixedRate()) {
      int healthCheckTime = (int) (ngbatisConfig.getCheckFixedRate() / 1000);
      sessionPoolConfig.setHealthCheckTime(healthCheckTime);
    }

    return new SessionPool(sessionPoolConfig);
  }

  @Override
  public void handleSession(LocalSession localSession, ResultSet result) {
    if (localSession != null) {
      boolean sessionError = ResultSetUtil.isSessionError(result);
      if (sessionError || timeToRelease(localSession)) {
        release(localSession);
      } else {
        offer(localSession);
      }
    }
  }

  @Override
  public ResultSet executeWithParameter(
      String gql,
      Map<String, Object> params,
      String space,
      Map<String, Object> extraReturn) {

    Session session = null;
    LocalSession localSession = null;
    ResultSet result = null;
    boolean useSessionPool = SessionDispatcher.useSessionPool();
    try {
      if (useSessionPool) {
        SessionPool sessionPool = ENV.getSessionPool(space);
        if (sessionPool == null) {
          throw new QueryException(space + " sessionPool is null");
        }
        extraReturn.put("localSessionSpace", space);
        return sessionPool.execute(gql, params);
      } else {
        localSession = poll();
        
        String[] qlAndSpace = qlWithSpace(localSession, gql, space);
        gql = qlAndSpace[1];
        String autoSwitch = qlAndSpace[0] == null ? "" : qlAndSpace[0];
        session = localSession.getSession();
        String oldSessionSpace = localSession.getCurrentSpace();
        result = session.executeWithParameter(gql, params);

        localSession.setCurrentSpace(getSpace(result));
        handleSession(localSession, result);
        if (log.isDebugEnabled()) {
          extraReturn.put("localSessionSpace", oldSessionSpace);
          String currentSpace = localSession.getCurrentSpace();
          if (nullSafeEquals(currentSpace, autoSwitch)) {
            extraReturn.put("autoSwitch", autoSwitch);
          }
        }
        return result;
      }
    } catch (Exception e) {
      throw new QueryException("execute failed: " + e.getMessage(), e);
    }
  }

  private static String[] qlWithSpace(LocalSession localSession, String gql, String currentSpace)
      throws IOErrorException, BindSpaceFailedException {
    String[] qlAndSpace = new String[2];
    gql = gql.trim();
    String sessionSpace = localSession.getCurrentSpace();
    boolean sameSpace = Objects.equals(sessionSpace, currentSpace);
    if (!sameSpace && currentSpace !=  null) {
      qlAndSpace[0] = currentSpace;
      Session session = localSession.getSession();
      ResultSet execute = session.execute(String.format("USE `%s`", currentSpace));
      if (!execute.isSucceeded()) {
        throw new BindSpaceFailedException(
          String.format(" %s \"%s\"", execute.getErrorMessage(), currentSpace)
        );
      }
    }
    qlAndSpace[1] = String.format("\n\t\t%s", gql);
    return qlAndSpace;
  }

  /**
   * 从结果集中获取当前的 space
   * @param result 脚本执行之后的结果集
   * @return 结果集所对应的 space
   */
  private static String getSpace(ResultSet result) {
    String spaceName = result.getSpaceName();
    return isBlank(spaceName) ? null : spaceName;
  }
}

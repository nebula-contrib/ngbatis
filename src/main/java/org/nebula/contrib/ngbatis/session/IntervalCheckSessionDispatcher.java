package org.nebula.contrib.ngbatis.session;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.proxy.MapperProxy.ENV;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.nebula.contrib.ngbatis.SessionDispatcher;
import org.nebula.contrib.ngbatis.config.EnvConfig;
import org.nebula.contrib.ngbatis.config.ParseCfgProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 间隔时间进行检查的本地会话调度器。
 * 
 * @author yeweicheng
 * @since 2022-08-26 2:34
 * <br>Now is history!
 */
public class IntervalCheckSessionDispatcher implements Runnable, SessionDispatcher {

  public static long SESSION_LIFE_LENGTH = 5 * 60 * 60 * 1000;
  public static long CHECK_FIXED_RATE = 5 * 60 * 60 * 1000; // unit ms
  private static Logger log = LoggerFactory.getLogger(IntervalCheckSessionDispatcher.class);
  private final NebulaPoolConfig nebulaPoolConfig;
  private final ArrayBlockingQueue<LocalSession> sessionQueue;
  private final ScheduledExecutorService threadPool;

  /**
   * 具备间隔时间做连接可用性检查的会话调度器
   * @param nebulaPoolConfig 连接信息
   */
  public IntervalCheckSessionDispatcher(NebulaPoolConfig nebulaPoolConfig, ParseCfgProps cfgProps) {
    this.nebulaPoolConfig = nebulaPoolConfig;
    this.sessionQueue = new ArrayBlockingQueue<>(nebulaPoolConfig.getMaxConnSize());
    threadPool = EnvConfig.reconnect ? Executors.newScheduledThreadPool(1) : null;
    //使用自定义的 session存活有效期/健康检测间隔
    SESSION_LIFE_LENGTH = cfgProps.getSessionLifeLength() == null ? SESSION_LIFE_LENGTH : cfgProps.getSessionLifeLength();
    CHECK_FIXED_RATE = cfgProps.getCheckFixedRate() == null ? CHECK_FIXED_RATE : cfgProps.getCheckFixedRate();
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
  }

  @Override
  public boolean timeToRelease(LocalSession session) {
    long birth = session.getBirth();
    return System.currentTimeMillis() - birth > SESSION_LIFE_LENGTH;
  }

}

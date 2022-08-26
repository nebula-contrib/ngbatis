package org.nebula.contrib.ngbatis.session;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.proxy.MapperProxy.env;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.nebula.contrib.ngbatis.SessionDispatcher;
import org.nebula.contrib.ngbatis.config.EnvConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 间隔时间进行检查的本地会话调度器
 *
 * @author yeweicheng
 * @since 2022-08-26 2:34 <br>
 *     Now is history!
 */
public class IntervalCheckSessionDispatcher
    implements Runnable, SessionDispatcher {

  private static Logger log = LoggerFactory.getLogger(
    IntervalCheckSessionDispatcher.class);

  private final NebulaPoolConfig nebulaPoolConfig;

  private final ArrayBlockingQueue<LocalSession> sessionQueue;
  private final ScheduledExecutorService threadPool;

  public static long sessionLifeLength = 5 * 60 * 60 * 1000;
  public static long checkFixedLength = 5 * 60 * 60 * 1000; // unit ms

  public IntervalCheckSessionDispatcher(
    final NebulaPoolConfig nebulaPoolConfig) {
        this.nebulaPoolConfig = nebulaPoolConfig;
        this.sessionQueue = new ArrayBlockingQueue<>(
        nebulaPoolConfig.getMaxConnSize());
        threadPool = EnvConfig.reconnect
          ? Executors.newScheduledThreadPool(1) : null;
        wakeUp();
    }

  @Override
  public void run() {
    for (LocalSession session : sessionQueue) {
      log.info(
          "LocalSession in queue which created at {}, useCount: {}",
          session.getBirth(),
          session.useCount);

      boolean finished = timeToRelease(session);
      if (finished || !session.getSession().ping()) {
        log.info("Release a session which created at {}", session.getBirth());
        release(session);
      }
    }
    while (sessionQueue.size() < nebulaPoolConfig.getMinConnSize()) {
      offer();
    }
  }

  @Override
  public void offer(final LocalSession session) {
    boolean offer = sessionQueue.offer(session);
    if (!offer) {
      releaseInnerSession(session);
    }
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
      localSession = sessionQueue.poll(
        nebulaPoolConfig.getWaitTime(), TimeUnit.MILLISECONDS);
      localSession = localSession == null ? newLocalSession() : localSession;
      localSession.useCount++;
      return localSession;
    } catch (InterruptedException e) {
      return newLocalSession();
    }
  }

  private void wakeUp() {
    if (threadPool != null) {
      threadPool.scheduleAtFixedRate(
        this, 3L, checkFixedLength, TimeUnit.MILLISECONDS);
    }
  }

  private void offer() {
    LocalSession localSession = newLocalSession();
    offer(localSession);
  }

  private LocalSession newLocalSession() {
    return new LocalSession(System.currentTimeMillis(), env.openSession());
  }

  private void releaseInnerSession(final LocalSession session) {
    session.getSession().release();
  }

  private void release(final LocalSession session) {
    session.getSession().release();
    sessionQueue.remove(session);
  }

  private boolean timeToRelease(final LocalSession session) {
    long birth = session.getBirth();
    return System.currentTimeMillis() - birth > sessionLifeLength;
  }
}

package org.nebula.contrib.ngbatis;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.nebula.contrib.ngbatis.config.NgbatisConfig;
import org.nebula.contrib.ngbatis.models.MapperContext;
import org.nebula.contrib.ngbatis.session.LocalSession;

/**
 * 本地会话调度器接口
 *
 * @author yeweicheng
 * @since 2022-08-26 4:34
 * <br>Now is history!
 */
public interface SessionDispatcher {

  void offer(LocalSession session);

  LocalSession poll();

  void release(LocalSession session);
  
  void releaseAll();

  /**
   * 判断session是否需要被释放
   * @param session 含有创建时间的session
   * @return 需要释放-true,不需要释放-false
   */
  boolean timeToRelease(LocalSession session);

  /**
   * Add space name to init the session in session pool.<br>
   * 将需要初始化的空间名添加到列表并在 sessionPool 中初始化 session.
   *
   * @author gin soul [create]
   * @author CorvusYe [refac]
   * @param space The session space in the session pool that needs to be init.
   * @since 1.1.2
   */
  static void addSpaceToSessionPool(String space) {
    boolean addable = useSessionPool() && isNotBlank(space);
    if (addable) {
      MapperContext.newInstance().getSpaceNameSet().add(space);
    }
  }
  
  /**
   *  Is ngbatis use nebula-java session pool?
   *  读取配置判断是否使用 nebula-java 的会话池
   *  
   *  <pre><code lang="yml">
   *    nebula:
   *      ngbatis:
   *        use-session-pool: true
   *  </code></pre>
   *  
   * @return Flag used by the session pool.
   * @author gin soul [create]
   * @author CorvusYe [refac]
   */
  static boolean useSessionPool() {
    NgbatisConfig ngbatisConfig = MapperContext.newInstance().getNgbatisConfig();
    return ngbatisConfig != null && ngbatisConfig.getUseSessionPool();
  }
}

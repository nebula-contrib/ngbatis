package org.nebula.contrib.ngbatis;

// Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.nebula.contrib.ngbatis.session.LocalSession;

/**
 * 本地会话调度器接口
 *
 * @author yeweicheng
 * @since 2022-08-26 4:34
 * <br>Now is history!
 */
public interface SessionDispatcher {
    void offer( LocalSession session );
    LocalSession poll();
}

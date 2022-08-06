// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package org.nebula.contrib.ngbatis.exception;

/**
 * 查询数据库时发生的异常
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class QueryException extends RuntimeException {
    public QueryException () {}
    public QueryException(String o) {
        super(o);;
    }
}

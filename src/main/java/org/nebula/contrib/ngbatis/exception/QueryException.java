package org.nebula.contrib.ngbatis.exception;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

/**.
 * 查询数据库时发生的异常.
 *.
 * @author yeweicheng <br>.
 *     Now is history.
.*/
public class QueryException extends RuntimeException {
  public QueryException() { }

  public QueryException(final String o) {
    super(o);
    ;
  }
}

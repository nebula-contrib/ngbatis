package org.nebula.contrib.ngbatis.exception;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

/**
 * 查询数据库时发生的异常
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class QueryException extends RuntimeException {

  /**
   * Set by {@link com.vesoft.nebula.client.graph.data.ResultSet#getErrorCode()}
   * <br>
   * Will be null when the exception is not caused by nGQL
   */
  private Integer code;

  public QueryException() {
  }

  public QueryException(String o) {
    super(o);
  }

  public QueryException(String o, Integer code) {
    super(o);
    this.code = code;
  }

  public QueryException(String o, Throwable e) {
    super(o, e);
  }
}

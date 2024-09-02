package org.nebula.contrib.kg.utils;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.io.Serializable;
import lombok.Data;

/**
 * @author yeweicheng
 * @since 2024-09-02 16:03
 * <br>Now is history!
 */
@Data
public class R<T> implements Serializable {

  private static final long serialVersionUID = 1030781725265793055L;

  private T data;

  private String message;

  private boolean success = true;

  private String code;
  
  public static <T> R<T> ok(T o) {
      return R.success( "200", null, o );
  }

  public static <T> R<T> success(String code, String message, T data) {
    R<T> result = new R<>();
    result.setData(data);
    result.setCode(code);
    result.setSuccess(true);
    result.setMessage(message);
    return result;
  }

}

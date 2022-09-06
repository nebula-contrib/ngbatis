package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

/**
 * 模糊查询
 *
 * @author yeweicheng
 * @since 2022-09-03 9:40
 * <br>Now is history!
 */
public class IfStringLike extends AbstractFunction<Object, Class<?>, String, Void, Void, Void> {

  @Override
  public String call(Object value, Class<?> type, String valueName) {
    boolean prepared = valueName != null;
    if (prepared && type != null) {
      if (value != null) {
        if (type == String.class) {
          return "=~ '.*' + $" + valueName + "+ '.*'";
        } else {
          return "== $" + valueName;
        }
      } else {
        return "is null";
      }
    } else {
      return call(value);
    }
  }
  
  @Override
  public String call(Object value) {
    if (value != null) {
      if (value instanceof String) {
        return "=~ '.*" + value + ".*'";
      } else {
        return "==" + value;
      }
    } else {
      return "is null";
    }
  }

}

package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getPkField;

/**
 * 通过实体，获取主键名
 *
 * @author yeweicheng
 * @since 2022-09-01 19:52
 * <br>Now is history!
 */
public class PkNameFn extends AbstractFunction<Object, Void, Void, Void, Void, Void> {

  @Override
  public String call(Object entity) {
    return getPkField(entity.getClass(), true).getName();
  }

}

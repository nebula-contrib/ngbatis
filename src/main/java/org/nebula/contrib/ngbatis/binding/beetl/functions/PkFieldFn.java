package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getPkField;

import java.lang.reflect.Field;

/**
 * 通过实体类，获取主键属性
 *
 * @author yeweicheng
 * @since 2022-08-25 2:58
 * <br>Now is history!
 */
public class PkFieldFn extends AbstractFunction<Class<?>, Boolean, Void, Void, Void, Void> {

  @Override
  public Field call(Class<?> entityType, Boolean idRequired) {
    return getPkField(entityType, idRequired);
  }

}

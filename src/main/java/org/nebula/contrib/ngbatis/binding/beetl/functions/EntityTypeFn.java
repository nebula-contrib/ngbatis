package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

/**
 * 通过实体，获取实体类
 *
 * @author yeweicheng
 * @since 2022-09-01 19:48
 * <br>Now is history!
 */
public class EntityTypeFn extends AbstractFunction<Object, Void, Void, Void, Void, Void> {

  @Override
  public Class<?> call(Object entity) {
    return entity.getClass();
  }

}

package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.List;

/**
 * 通过实体对象，获取 id 值
 *
 * @author yeweicheng
 * @since 2023-11-21 19:29
 * <br>Now is history!
 */
public class GetFn extends AbstractFunction<List<?>, Integer, Boolean, Void, Void, Void> {
  @Override
  public Object call(List<?> collection, Integer index) {
    return collection.get(index);
  }
}

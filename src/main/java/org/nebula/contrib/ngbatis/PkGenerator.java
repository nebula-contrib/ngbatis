package org.nebula.contrib.ngbatis;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

/**
 * 主键生成器接口<br> 项目中可通过实现当前接口，并注册成 Component 即可完成注入
 *
 * @author yeweicheng
 * @since 2022-06-14 11:39
 * <br>Now is history!
 */
public interface PkGenerator {

  <T> T generate(String tagName, Class<T> pkType);

}

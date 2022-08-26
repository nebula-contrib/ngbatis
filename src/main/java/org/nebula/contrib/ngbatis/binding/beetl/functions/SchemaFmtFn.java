package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

/**.
 * 通过对 schema 名称进行格式化，规避与数据库关键字的冲突.
 *.
 * @author yeweicheng.
 * @since 2022-08-25 4:06 <br>.
 *     Now is history.
.*/
public class SchemaFmtFn extends AbstractFunction
    <String, String, Void, Void, Void, Void> {
  @Override
  public Object call(final Object[] paras) {
    return String.format("`%s`", paras[0]);
  }
}

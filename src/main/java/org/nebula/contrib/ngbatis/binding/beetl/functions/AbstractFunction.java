package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.apache.commons.lang3.NotImplementedException;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.springframework.util.Assert;

/**
 * Beetl Function在ngbatis中的抽象方法。
 * @author yeweicheng
 * @since 2022-08-25 5:41
 * <br>Now is history!
 */
public abstract class AbstractFunction<A, B, C, D, E, F> implements Function {

  protected Context ctx = null;

  protected String valueFmtFn = "ng.valueFmt";
  protected String schemaFmtFn = "ng.schemaFmt";
  protected String tagNameFn = "ng.tagName";
  protected String pkFieldFn = "ng.pkField";
  protected String idFn = "ng.id";
  protected String kvFn = "ng.kv";
  protected String joinFn = "ng.join";

  @Override
  public Object call(Object[] paras, Context ctx) {
    this.ctx = ctx;
    return call(paras);
  }

  /**
   * 提供基类方法，充当根据参数个数调用子类实现方法的路由。
   * @param paras 模板函数的参数
   * @return 最终体现在模板内的内容
   */
  @SuppressWarnings("unchecked")
  public Object call(Object[] paras) {
    int len = paras.length;

    // 限制 nGQL 使用 ng 函数时的长度，避免参数过长影响编写体验
    Assert.isTrue(len <= 6,
        "ng-function cannot have more than 6 parameters"
    );

    return call(
      len > 0 ? (A) paras[0] : null,
      len > 1 ? (B) paras[1] : null,
      len > 2 ? (C) paras[2] : null,
      len > 3 ? (D) paras[3] : null,
      len > 4 ? (E) paras[4] : null,
      len > 5 ? (F) paras[5] : null
    );
  }

  /**
   * 函数调用基类方法。
   * @param p0 第一个参数
   * @param p1 第二个参数
   * @param p2 第三个参数
   * @param p3 第四个参数
   * @param p4 第五个参数
   * @param p5 第六个参数
   * @return 最终体现在模板内的内容
   */
  public Object call(A p0, B p1, C p2, D p3, E p4, F p5) {
    if (p5 == null) {
      return call(p0, p1, p2, p3, p4);
    }
    throw new NotImplementedException();
  }

  /**
   * 函数调用基类方法。
   * @param p0 第一个参数
   * @param p1 第二个参数
   * @param p2 第三个参数
   * @param p3 第四个参数
   * @param p4 第五个参数
   * @return 最终体现在模板内的内容
   */
  public Object call(A p0, B p1, C p2, D p3, E p4) {
    if (p4 == null) {
      return call(p0, p1, p2, p3);
    }
    throw new NotImplementedException();
  }

  /**
   * 函数调用基类方法。
   * @param p0 第一个参数
   * @param p1 第二个参数
   * @param p2 第三个参数
   * @param p3 第四个参数
   * @return 最终体现在模板内的内容
   */
  public Object call(A p0, B p1, C p2, D p3) {
    if (p3 == null) {
      return call(p0, p1, p2);
    }
    throw new NotImplementedException();
  }

  /**
   * 函数调用基类方法。
   * @param p0 第一个参数
   * @param p1 第二个参数
   * @param p2 第三个参数
   * @return 最终体现在模板内的内容
   */
  public Object call(A p0, B p1, C p2) {
    if (p2 == null) {
      return call(p0, p1);
    }
    throw new NotImplementedException();
  }

  /**
   * 函数调用基类方法。
   * @param p0 第一个参数
   * @param p1 第二个参数
   * @return 最终体现在模板内的内容
   */
  public Object call(A p0, B p1) {
    if (p1 == null) {
      return call(p0);
    }
    throw new NotImplementedException();
  }

  /**
   * 函数调用基类方法。
   * @param p0 第一个参数
   * @return 最终体现在模板内的内容
   */
  public Object call(A p0) {
    if (p0 == null) {
      return call();
    }
    throw new NotImplementedException();
  }

  /**
   * 函数调用基类方法。
   * @return 最终体现在模板内的内容
   */
  public Object call() {
    throw new NotImplementedException();
  }

  /**
   * 函数调用基类方法。
   * @param fnName 调用的 beetl function 的函数名
   * @param args 模板函数的参数
   * @return 最终体现在模板内的内容
   */
  public <T> T fnCall(Object fnName, Object... args) {
    return (T) ctx.gt.getFunction(String.valueOf(fnName)).call(args, ctx);
  }

}

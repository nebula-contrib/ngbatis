package org.nebula.contrib.ngbatis.annotations;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于计算方法执行时间的注解。被切面 {@link org.nebula.contrib.ngbatis.aop.LogAdvice } 所监听。
 *
 * @author yeweicheng <br>
 *     Now is history!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TimeLog {

  String explain();

  String name();
}

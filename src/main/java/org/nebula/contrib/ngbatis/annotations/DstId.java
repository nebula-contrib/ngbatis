package org.nebula.contrib.ngbatis.annotations;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 用于标记目标ID的注解。
 * 
 * @author yeweicheng
 * @since 2024-07-18 17:27
 * <br>Now is history!
 */
@Target(ElementType.FIELD)
@Retention(RUNTIME)
public @interface DstId {

}

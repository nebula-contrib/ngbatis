// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于计算方法执行时间的注解。被切面 {@link ye.weicheng.ngbatis.aop.LogAdvice } 所监听。
 *
 * @author yeweicheng
 * <br>Now is history!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( ElementType.METHOD)
public @interface TimeLog {

    String explain();

    String name();

}

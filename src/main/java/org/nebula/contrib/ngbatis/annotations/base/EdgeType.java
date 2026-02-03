package org.nebula.contrib.ngbatis.annotations.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记边类型的注解
 * @author xYLiuuuuuu
 * @since 2024/9/8 13:17
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EdgeType {
  String name();
}

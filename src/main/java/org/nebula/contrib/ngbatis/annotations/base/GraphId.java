package org.nebula.contrib.ngbatis.annotations.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nebula.contrib.ngbatis.enums.IdType;

/**
 * @author xYLiuuuuuu
 * @since 2024/9/8 10:14
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphId {
  IdType type();
}

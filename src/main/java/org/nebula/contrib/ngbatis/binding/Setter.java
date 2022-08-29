package org.nebula.contrib.ngbatis.binding;

/**
 * nebula-client数据类型设值器
 * @author yeweicheng
 * @since 2022-08-29 8:01
 * <br>Now is history!
 */
public interface Setter<T> {

  Object set(T param);
}

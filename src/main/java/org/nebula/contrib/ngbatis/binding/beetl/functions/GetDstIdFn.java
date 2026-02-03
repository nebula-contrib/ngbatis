package org.nebula.contrib.ngbatis.binding.beetl.functions;

import java.lang.reflect.Field;
import org.nebula.contrib.ngbatis.annotations.DstId;
import org.nebula.contrib.ngbatis.annotations.SrcId;
import org.nebula.contrib.ngbatis.utils.ReflectUtil;

/**
 * @author yeweicheng
 * @since 2025-12-13 8:56
 * <br>Now is history!
 */
public class GetDstIdFn extends AbstractFunction<Object, Void, Void, Void, Void, Void> {

  @Override
  public Object call(Object o) {
    try {
      Field dstIdField = ReflectUtil.getAnnoField(o.getClass(), DstId.class);
      Object dstId = ReflectUtil.getValue(o, dstIdField);
      return fnCall(valueFmtFn, dstId);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

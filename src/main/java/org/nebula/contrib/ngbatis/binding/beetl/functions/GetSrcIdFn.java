package org.nebula.contrib.ngbatis.binding.beetl.functions;

import java.lang.reflect.Field;
import org.nebula.contrib.ngbatis.annotations.SrcId;
import org.nebula.contrib.ngbatis.utils.ReflectUtil;

/**
 * @author yeweicheng
 * @since 2025-12-13 9:02
 * <br>Now is history!
 */
public class GetSrcIdFn extends AbstractFunction<Object, Void, Void, Void, Void, Void> {

  @Override
  public Object call(Object o) {
    try {
      Field srcIdField = ReflectUtil.getAnnoField(o.getClass(), SrcId.class);
      Object srcId = ReflectUtil.getValue(o, srcIdField);
      return fnCall(valueFmtFn, srcId);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

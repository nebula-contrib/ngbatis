package org.nebula.contrib.ngbatis.binding;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

/**
 * <p>提供给fastjson的时间序列化器。</p>
 * @author yeweicheng
 * @since 2022-08-29 8:06
 *     Now is history!
 */
@Deprecated
public class DateDeserializer implements ObjectSerializer {

  @Override
  public void write(
      JSONSerializer serializer, 
      Object object, 
      Object fieldName, 
      Type type,
      int i) {
    SerializeWriter out = serializer.getWriter();
    if (object == null) {
      out.writeNull();
      return;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    String fn = "datetime";
    Class<?> objClass = object.getClass();
    fn = objClass == java.util.Date.class ? "datetime"
      : objClass == java.sql.Date.class ? "date"
        : objClass == java.sql.Time.class ? "time"
          : fn;
    out.write("\"" + String.format("%s('%s')", fn, sdf.format(object)) + "\"");
  }
}

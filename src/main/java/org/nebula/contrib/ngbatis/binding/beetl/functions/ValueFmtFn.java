package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import java.text.SimpleDateFormat;
import java.util.Date;

/**.
 * 对传递给数据库的值进行不同类型的格式化.
 *.
 * @author yeweicheng.
 * @since 2022-08-24 15:52 <br>.
 *     Now is history.
.*/
public class ValueFmtFn extends AbstractFunction
    <Object, Void, Void, Void, Void, Void> {

  @Override
  public Object call(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof String) return "'" + value + "'";

    if (value instanceof Date) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
      String fn = "datetime";
      Class<?> objClass = value.getClass();
      fn =
          objClass == java.util.Date.class
              ? "datetime"
              : objClass == java.sql.Date.class
                  ? "date"
                  : objClass == java.sql.Time.class ? "time" : fn;
      return String.format("%s('%s')", fn, sdf.format(value));
    }
    return value;
  }
}

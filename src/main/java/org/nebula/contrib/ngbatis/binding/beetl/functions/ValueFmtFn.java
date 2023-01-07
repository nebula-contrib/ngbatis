package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.text.StringEscapeUtils;

/**
 * 对传递给数据库的值进行不同类型的格式化
 *
 * @author yeweicheng
 * @since 2022-08-24 15:52
 * <br>Now is history!
 */
public class ValueFmtFn extends AbstractFunction<Object, Boolean, Boolean, Void, Void, Void> {

  private static boolean escape = true;
  
  public static void setEscape(boolean escape) {
    ValueFmtFn.escape = escape;
  }

  @Override
  public Object call(Object value, Boolean ifStringLike, Boolean escape) {
    ifStringLike = ifStringLike != null && ifStringLike;
    escape = escape != null ? escape : ValueFmtFn.escape;
    if (value == null) {
      return null;
    }
    if (value instanceof String) {
      return ifStringLike 
        ? "'.*" + value + ".*'"
        : "'" + (escape ? StringEscapeUtils.escapeJava((String) value) : value) + "'";
    }
    
    if (value instanceof BigDecimal) {
      return ((BigDecimal) value).toPlainString();
    }

    if (value instanceof Date) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
      String fn = "datetime";
      Class<?> objClass = value.getClass();
      fn = objClass == java.util.Date.class ? "datetime"
        : objClass == java.sql.Date.class ? "date"
          : objClass == java.sql.Time.class ? "time"
            : fn;
      return String.format("%s('%s')", fn, sdf.format(value));
    }
    return value;
  }
}

package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
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

  private static final String DATE_FMT = "yyyy-MM-dd";
  private static final String TIME_FMT = "HH:mm:ss.SSS";
  private static final String DATETIME_FMT = String.format("%s'T'%s", DATE_FMT, TIME_FMT);
  
  private static boolean escape = true;

  private static String parameterQuote = "\"";
  
  public static void setEscape(boolean escape) {
    ValueFmtFn.escape = escape;
  }
  
  public static void setParameterQuote(String parameterQuote) {
    ValueFmtFn.parameterQuote = parameterQuote;
  }

  @Override
  public Object call(Object value, Boolean ifStringLike, Boolean escape) {
    ifStringLike = ifStringLike != null && ifStringLike;
    escape = escape != null ? escape : ValueFmtFn.escape;
    if (value == null) {
      return null;
    }
    if (value instanceof String) {
      value = (escape ? StringEscapeUtils.escapeJava((String) value) : value);
      return ifStringLike 
        ? String.format("%s.*%s.*%s", parameterQuote, value, parameterQuote)
        : String.format("%s%s%s", parameterQuote, value, parameterQuote);
    }
    
    if (value instanceof BigDecimal) {
      return ((BigDecimal) value).toPlainString();
    }
    
    if (value instanceof Duration) {
      return String.format(
        "duration({seconds: %d})",
        ((Duration) value).getSeconds()
      );
    }

    if (value instanceof Date) {
      Class<?> objClass = value.getClass();
      if (objClass == Timestamp.class) {
        // 数据库时间戳的单位是秒
        return String.format("%s(%d)", "timestamp", (((Timestamp) value).getTime() / 1000));
      }

      String timePattern = objClass == java.util.Date.class ? DATETIME_FMT
        : objClass == java.sql.Date.class ? DATE_FMT
          : objClass == java.sql.Time.class ? TIME_FMT
            : DATETIME_FMT;
      SimpleDateFormat sdf = new SimpleDateFormat(timePattern);
      
      String fn = "datetime";
      fn = objClass == java.util.Date.class ? "datetime"
        : objClass == java.sql.Date.class ? "date"
          : objClass == java.sql.Time.class ? "time"
            : fn;
      return String.format("%s('%s')", fn, sdf.format(value));
    }
    return value;
  }
}

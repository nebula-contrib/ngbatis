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
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;

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
    
    //    ST_Point(longitude, latitude)
    if (value instanceof Point) {
      Point point = (Point) value;
      return String.format(
        "ST_Point(%s, %s)",
        point.getX(),
        point.getY()
      );
    }
    
    //    ST_GeogFromText("LINESTRING(3 8, 4.7 73.23)")
    if (value instanceof Box) {
      Box box = (Box) value;
      Point first = box.getFirst();
      Point second = box.getSecond();
      return String.format(
        "ST_GeogFromText(\"LINESTRING(%s %s, %s %s)\")",
        first.getX(),
        first.getY(),
        second.getX(),
        second.getY()
      );
    }
    
    if (value instanceof Polygon) {
      Polygon polygon = (Polygon) value;
      StringBuilder sb = new StringBuilder("ST_GeogFromText(\"POLYGON((");
      for (Point point : polygon.getPoints()) {
        sb.append(String.format("%s %s,", point.getX(), point.getY()));
      }
      
      // 追加起始点，形成闭环      
      Point point = polygon.getPoints().get(0);
      sb.append(String.format("%s %s,", point.getX(), point.getY()));
      
      sb.deleteCharAt(sb.length() - 1);
      sb.append("))\")");
      return sb.toString();
    }
    
    return value;
  }
}

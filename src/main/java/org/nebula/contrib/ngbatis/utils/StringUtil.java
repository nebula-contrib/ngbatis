package org.nebula.contrib.ngbatis.utils;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.Arrays;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

/**
 * 字符串工具类。
 *
 * @author yeweicheng
 * @since 2022-06-12 23:25
 * <br>Now is history!
 */
public abstract class StringUtil {

  /**
   * 驼峰格式转下划线。
   *
   * @param camel 驼峰格式字符串
   * @return 下划线字符串
   */
  public static String camelToUnderline(String camel) {
    String[] splitByCamel =
      StringUtils.splitByCharacterTypeCamelCase(camel);
    Iterator<String> iterator = Arrays.stream(splitByCamel).iterator();
    return Strings.join(iterator, '_').toLowerCase();
  }

}

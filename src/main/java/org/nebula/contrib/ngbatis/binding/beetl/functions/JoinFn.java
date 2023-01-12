package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用 String.join， 在集合中，加入间隔符，并形成新的字符串返回
 *
 * @author yeweicheng
 * @since 2022-08-25 3:59
 * <br>Now is history!
 */
public class JoinFn extends AbstractFunction<Iterable<?>, String, String, Void, Void, Void> {

  /**
   * 模板函数：支持在xml中调用。可快速完成元素间的拼接。
   * <em>ng.join</em>
   * @param iterable 参与拼接的集合
   * @param sep 连接用的分隔符
   * @param fnName 元素拼接前调用的函数（格式化函数）
   * @return 集合拼接后的字符串
   */
  public String call(Iterable<?> iterable, String sep, String fnName) {
    sep = sep == null ? "," : sep;
    List<String> strs = new ArrayList<>();
    for (Object el : iterable) {
      String elStr = isEmpty(fnName) ? String.valueOf(el)
          : String.valueOf((Object)fnCall(fnName, el));
      strs.add(elStr);
    }

    return String.join(sep, strs);
  }

}

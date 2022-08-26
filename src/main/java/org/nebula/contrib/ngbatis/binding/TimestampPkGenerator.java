package org.nebula.contrib.ngbatis.binding;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import org.nebula.contrib.ngbatis.PkGenerator;

/**.
 * 主键生成样例.
 *.
 * @author yeweicheng.
 * @since 2022-06-14 12:32 <br>.
 *     Now is history.
.*/
public class TimestampPkGenerator implements PkGenerator {

  @Override
  public <T> T generate(final String tagName, final Class<T> pkType) {
    Long id = System.currentTimeMillis();
    if (pkType == String.class) {
      return (T) String.valueOf(id);
    }
    return (T) id;
  }
}

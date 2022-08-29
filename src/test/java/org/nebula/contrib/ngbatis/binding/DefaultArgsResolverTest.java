package org.nebula.contrib.ngbatis.binding;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * @author yeweicheng
 * @since 2022-06-22 14:45
 * <br>Now is history!
 */
class DefaultArgsResolverTest {

  @Test
  void customToJSON() throws IOException {

    DefaultArgsResolver defaultArgsResolver = new DefaultArgsResolver();
    Map<String, Object> o = new HashMap<String, Object>() {{
      put("dt", new Date());
      put("t", new java.sql.Time(System.currentTimeMillis()));
      put("d", new java.sql.Date(System.currentTimeMillis()));
      put("o", null);
    }};
    Object o1 = defaultArgsResolver.customToJson(o);
    System.out.println(o1);
  }
}
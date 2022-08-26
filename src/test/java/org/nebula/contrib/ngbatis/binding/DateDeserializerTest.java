package org.nebula.contrib.ngbatis.binding;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * @author yeweicheng
 * @since 2022-06-22 4:44 <br>
 *     Now is history!
 */
class DateDeserializerTest {

  @Test
  public void test() {
    DateDeserializer dateDeserializer = new DateDeserializer();
    SerializeConfig cfg = new SerializeConfig();
    Map<String, Date> o =
        new HashMap<String, Date>() {
          {
            put("t", new Date());
          }
        };
    cfg.put(Date.class, dateDeserializer);
    System.out.println(JSON.toJSON(o, cfg));
    String o1 = JSON.toJSONString(o, cfg);
    Object o2 = JSON.parse(o1);
    System.out.println(o2);
  }
}
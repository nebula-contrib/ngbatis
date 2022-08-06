// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package org.nebula.contrib.ngbatis.binding;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yeweicheng
 * @since 2022-06-22 4:44
 * <br>Now is history!
 */
class DateDeserializerTest {

    @Test
    public void test() {
        DateDeserializer dateDeserializer = new DateDeserializer();
        SerializeConfig cfg = new SerializeConfig();
        Map<String, Date> o = new HashMap<String, Date>() {{
            put("t", new Date());
        }};
        cfg.put( Date.class, dateDeserializer );
        System.out.println( JSON.toJSON( o, cfg ));
        String o1 = JSON.toJSONString(o, cfg);
        Object o2 = JSON.parse(o1);
        System.out.println( o2 );
    }

}
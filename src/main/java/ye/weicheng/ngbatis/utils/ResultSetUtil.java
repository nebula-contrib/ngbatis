// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.utils;

import com.vesoft.nebula.client.graph.data.ValueWrapper;

import java.io.UnsupportedEncodingException;

/**
 * 结果集基础类型处理的工具类
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class ResultSetUtil {

    public  static <T> T getValue(ValueWrapper value) {
        try {
            Object o = value.isLong() ? value.asLong()
                    : value.isBoolean() ? value.asBoolean()
                    : value.isDouble() ? value.asDouble()
                    : value.isString() ? value.asString()
                    : value.isTime() ? value.asTime()
                    : value.isDate() ? value.asDate()
                    : value.isDateTime() ? value.asDateTime()
                    : value.isVertex() ? value.asNode()
                    : value.isEdge() ? value.asRelationship()
                    : value.isPath() ? value.asPath()
                    : value.isList() ? value.asList()
                    : value.isSet() ? value.asList()
                    : value.isMap() ? value.asMap()
                    : null;

            return (T)o;
        } catch (UnsupportedEncodingException e) {
           throw new RuntimeException( e );
        }
    }
}

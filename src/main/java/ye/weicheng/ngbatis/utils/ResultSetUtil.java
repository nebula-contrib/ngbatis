// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.utils;

import com.vesoft.nebula.client.graph.data.Node;
import com.vesoft.nebula.client.graph.data.Relationship;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import ye.weicheng.ngbatis.exception.ResultHandleException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ye.weicheng.ngbatis.proxy.NebulaDaoBasicExt.getPkField;
import static ye.weicheng.ngbatis.utils.ReflectUtil.castNumber;

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
                    : value.isList() ? transformList( value.asList() )
                    : value.isSet() ? transformList( value.asList() )
                    : value.isMap() ? transformMap( value.asMap() )
                    : null;

            return (T)o;
        } catch (UnsupportedEncodingException e) {
           throw new RuntimeException( e );
        }
    }

    private static Object transformMap(HashMap<String, ValueWrapper> map) {
        HashMap<Object, Object> javaResult = new HashMap<>();
        for (Map.Entry<String, ValueWrapper> entry : map.entrySet()) {
            String k = entry.getKey();
            ValueWrapper v = entry.getValue();
            javaResult.put( k, getValue( v ));
        }
        return javaResult;
    }

    private static Object transformList(ArrayList<ValueWrapper> list) {
        return list.stream().map(ResultSetUtil::getValue).collect(Collectors.toList());
    }

    public static <T> T getValue(ValueWrapper valueWrapper, Class<T> resultType) {
        T value = getValue( valueWrapper );
        if (value instanceof  Number ) {
            value = (T) castNumber( (Number) value, resultType );
        }
        return value;
    }

    public static <T> T nodeToResultType(Node v, Class<T> resultType) {
        T t = null;
        try {
            List<ValueWrapper> values = v.values(v.tagNames().get(0));
            List<String> keys = v.keys(v.tagNames().get(0));
            t = resultType.newInstance();
            for (int i = 0; i < keys.size(); i++) {
                String prop = keys.get( i );
                ReflectUtil.setValue( t, prop, ResultSetUtil.getValue( values.get( i ) ));
            }
            setId( t, resultType, v);
        } catch (UnsupportedEncodingException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            throw new ResultHandleException( String.format( "%s : %s", e.getClass().toString(), e.getMessage() ) );
        }
        return t;
    }

    public static void nodeToResultType(Object o, String fieldName, Node node) {
        Class<?> fieldType = ReflectUtil.fieldType( o, fieldName );
        if( fieldType != null ) {
            Object fieldValue = nodeToResultType(node, fieldType);
            try {
                ReflectUtil.setValue( o, fieldName, fieldValue );
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> T relationshipToResultType(Relationship  r, Class<T> resultType) {
        T t = null;
        try {
            t = resultType.newInstance();
            HashMap<String, ValueWrapper> properties = r.properties();
            for (Map.Entry<String, ValueWrapper> entry : properties.entrySet()) {
                ReflectUtil.setValue( t, entry.getKey(), ResultSetUtil.getValue( entry.getValue() ));
            }
        } catch (UnsupportedEncodingException | InstantiationException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static void relationshipToResultType(Object o, String fieldName, Relationship relationship) {
        Class<?> fieldType = ReflectUtil.fieldType( o, fieldName );
        if( fieldType != null ) {
            Object fieldValue = relationshipToResultType(relationship, fieldType);
            try {
                ReflectUtil.setValue( o, fieldName, fieldValue );
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setId( Object obj, Class<?> resultType, Node v ) throws IllegalAccessException {
        Field pkField = getPkField( resultType );
        ValueWrapper idWrapper = v.getId();
        Object id = ResultSetUtil.getValue(idWrapper);
        ReflectUtil.setValue( obj, pkField, id );
    }

}

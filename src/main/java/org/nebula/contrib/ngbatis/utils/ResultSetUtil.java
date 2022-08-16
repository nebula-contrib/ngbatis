package org.nebula.contrib.ngbatis.utils;

// Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.nebula.contrib.ngbatis.exception.ResultHandleException;
import org.nebula.contrib.ngbatis.models.MapperContext;
import org.nebula.contrib.ngbatis.proxy.MapperProxy;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.nebula.contrib.ngbatis.proxy.NebulaDaoBasicExt.getPkField;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.castNumber;

/**
 * 结果集基础类型处理的工具类
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class ResultSetUtil {

    private static Logger log = LoggerFactory.getLogger( ResultSetUtil.class );

    public  static <T> T getValue(ValueWrapper value) {
        try {
            Object o = value.isLong() ? value.asLong()
                    : value.isBoolean() ? value.asBoolean()
                    : value.isDouble() ? value.asDouble()
                    : value.isString() ? value.asString()
                    : value.isTime() ? value.asTime()
                    : value.isDate() ? transformDate( value.asDate() )
                    : value.isDateTime() ? transformDateTime( value.asDateTime() )
                    : value.isVertex() ? transformNode( value.asNode() )
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

    private static Object transformDateTime(DateTimeWrapper dateTime) {
        return new GregorianCalendar(
                dateTime.getYear(),
                dateTime.getMonth() - 1,
                dateTime.getDay(),
                dateTime.getHour(),
                dateTime.getMinute(),
                dateTime.getSecond()
        ).getTime();
    }

    private static Object transformDate(DateWrapper date ) {
        return new GregorianCalendar(date.getYear() + 1900, date.getMonth() - 1, date.getDay()).getTime();
    }


    private static Object transformNode(Node node) {
        List<String> tagNames = node.tagNames();
        if ( tagNames.size() != 1 ) {
            log.warn( "Sorry there is no parse implements for multi tags node: {}", node );
            return node;
        }

        String tagName = tagNames.get(0);

        MapperContext mapperContext = MapperProxy.ENV.getMapperContext();
        Map<String, Class<?>> tagTypeMapping = mapperContext.getTagTypeMapping();

        Class<?> nodeType = tagTypeMapping.get(tagName);
        if( nodeType != null ) {
            return nodeToResultType( node, nodeType );
        }
        return node;
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

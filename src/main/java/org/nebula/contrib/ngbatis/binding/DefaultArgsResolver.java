// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package org.nebula.contrib.ngbatis.binding;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vesoft.nebula.*;
import org.nebula.contrib.ngbatis.ArgsResolver;
import org.nebula.contrib.ngbatis.models.MethodModel;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.nebula.contrib.ngbatis.utils.ReflectUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static org.nebula.contrib.ngbatis.utils.ReflectUtil.isCurrentTypeOrParentType;

/**
 * 默认的参数解析器
 *
 * @author yeweicheng
 * <br>Now is history!
 */
@Component
public class DefaultArgsResolver implements ArgsResolver {
    @Override
    public Map<String, Object> resolve(MethodModel methodModel , Object... args) {
        Method method = methodModel.getMethod();
        if( args.length == 0 ) {
            return Collections.emptyMap();
        }
        int len = methodModel.getParameterCount();
        Map<String, Object> result = new HashMap<>();
        Annotation[][] parameterAnnotations = methodModel.getParameterAnnotations();
        for( int i = 0 ; i < len ; i ++ ) {
            Annotation[] annotationArgIndex = parameterAnnotations[i];
            int annoLen = annotationArgIndex == null ? 0 : annotationArgIndex.length;
            boolean notFoundParamAnno = true;
            for( int j = 0 ; j < annoLen ; j ++ ) {
                if( annotationArgIndex[j] instanceof Param ) {
                    Param annotationArgIndex1 = (Param) annotationArgIndex[j];
                    String key = annotationArgIndex1.value();
                    result.put( key, JSON.toJSON(args[ i ] ));
                    notFoundParamAnno = false;
                }
            }
            if( notFoundParamAnno ) {
                Class<?> paramClass = args[i].getClass();
                if( isBaseType(paramClass))
                    result.put( "p" + i,  JSON.toJSON(args[ i ] ) );
                else if ( args[i] instanceof Collection)
                    result.put( "p" + i, args[i] );
                else {
                    if( len == 1 ) {
                        result = (Map<String,Object>)customToJSON( args[0] );
                    } else {
                        result.put( "p" + i, customToJSON( args[i] ) );
                    }
                }
            }
        }
        return result;
    }

    public static Map<Class<?>, Setter> LEAF_TYPE_AND_SETTER = new HashMap<Class<?>, Setter>() {{
        put(boolean.class, (Setter<Boolean>) Value::bVal);
        put(Boolean.class, (Setter<Boolean>) Value::bVal);
        put(int.class, (Setter<Integer>) Value::iVal);
        put(Integer.class, (Setter<Integer>) Value::iVal);
        put(short.class, (Setter<Short>) Value::iVal);
        put(Short.class, (Setter<Short>) Value::iVal);
        put(byte.class, (Setter<Short>) Value::iVal);
        put(Byte.class, (Setter<Short>) Value::iVal);
        put(long.class, (Setter<Long>) Value::iVal);
        put(Long.class, (Setter<Long>) Value::iVal);
        put(float.class, (Setter<Float>) Value::fVal);
        put(Float.class, (Setter<Float>) Value::fVal);
        put(double.class, (Setter<Double>) Value::fVal);
        put(Double.class, (Setter<Double>) Value::fVal);
        put(byte[].class, (Setter<byte[]>) Value::sVal);
        put(String.class, (Setter<String>) (param) -> Value.sVal( param.getBytes() ));
        put(com.vesoft.nebula.Date.class, (Setter<com.vesoft.nebula.Date>) Value::dVal);
        put(Time.class, (Setter<Time>) Value::tVal);
        put(DateTime.class, (Setter<DateTime>) Value::dtVal);
        put(Vertex.class, (Setter<Vertex>) Value::vVal);
        put(Edge.class, (Setter<Edge>) Value::eVal);
        put(Path.class, (Setter<Path>) Value::pVal);
        put(NList.class, (Setter<NList>) Value::lVal);
        put(NMap.class, (Setter<NMap>) Value::mVal);
        put(NSet.class, (Setter<NSet>) Value::uVal);
        put(DataSet.class, (Setter<DataSet>) Value::gVal);
        put(Geography.class, (Setter<Geography>) Value::ggVal);
        put(Duration.class, (Setter<Duration>) Value::duVal);
    }};

    public static Map<Class<?>, Setter> COMPLEX_TYPE_AND_SETTER = new LinkedHashMap<Class<?>, Setter>() {{
        put( Set.class, (Setter<Set>) (set) -> {
            HashSet<Object> values = new HashSet<>();
            set.forEach( el -> values.add( toNebulaValueType( el )));
            return values;
        });

        put( Collection.class, (Setter<Collection>) (collection) -> {
            List<Object> list = new ArrayList<>();
            collection.forEach(el -> list.add( toNebulaValueType( el )));
            return list;
        });

        put( Map.class , (Setter<Map>) (map) -> {
            Map<Object, Object> valueMap = new HashMap<>();
            map.forEach( ( k, v ) -> {
                valueMap.put( toNebulaValueType( k ), toNebulaValueType( v ));
            });
            return valueMap;
        });

        put( Date.class, (Setter<Date>) (date) -> {
            Calendar calendar = new Calendar.Builder().setInstant(date).build();
            return Value.dtVal( new DateTime(
                    new Short(String.valueOf( calendar.get( Calendar.YEAR  ) ) ),
                    new Byte(String.valueOf( calendar.get( Calendar.MONTH ) ) ),
                    new Byte(String.valueOf( calendar.get( Calendar.DATE ) ) ),
                    new Byte(String.valueOf( calendar.get( Calendar.HOUR ) ) ),
                    new Byte(String.valueOf( calendar.get( Calendar.MINUTE ) ) ),
                    new Byte(String.valueOf( calendar.get( Calendar.SECOND ) ) ),
                    new Short(String.valueOf( calendar.get( Calendar.MILLISECOND )))
            ));
        });

        put ( Object.class, (Setter<Object>) (obj) -> {
            Map<String, Object> pojoFields =  new HashMap<>();
            Class<?> paramType = obj.getClass();
            Field[] declaredFields = paramType.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                pojoFields.put( declaredField.getName(), toNebulaValueType( ReflectUtil.getValue( obj, declaredField ) ) );
            }
            return  pojoFields;
        });
    }};

    public static <T> T toNebulaValueType( Object param ) {
        if( param == null ) {
            return null;
        }
        Class<?> paramType = param.getClass();
        Setter setter = LEAF_TYPE_AND_SETTER.get(paramType);
        if( setter != null ) return (T)setter.set( param );
        for (Class<?> pType : COMPLEX_TYPE_AND_SETTER.keySet()) {
            if( isCurrentTypeOrParentType( paramType, pType )  )  {
                return (T)COMPLEX_TYPE_AND_SETTER.get( pType ).set( param );
            }
        }
        return  (T)param;
    }




    public Object customToJSON( Object o ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SerializeConfig parserConfig = new SerializeConfig();
            parserConfig.put( Date.class, new DateDeserializer() );
            parserConfig.put( java.sql.Date.class, new DateDeserializer() );
            parserConfig.put( java.sql.Time.class, new DateDeserializer() );
            String text = JSON.toJSONString(o, parserConfig, SerializerFeature.WriteMapNullValue);
            text = text.replaceAll("\\\\n", "\\\\\\\\n");
            Map map = objectMapper.readValue(text, Map.class);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
//        return JSON.parseObject(text, Feature.AllowArbitraryCommas);
    }

    private boolean isBaseType( Class clazz ) {
        return  clazz == Character.class || clazz == char.class ||
                clazz == Byte.class || clazz == byte.class ||
                clazz == Short.class || clazz == short.class ||
                clazz == Integer.class || clazz == int.class ||
                clazz == Long.class || clazz == long.class ||
                clazz == Float.class || clazz == float.class ||
                clazz == Double.class || clazz == double.class ||
                clazz == Boolean.class || clazz == boolean.class ||
                clazz == String.class;
    }

}

interface Setter<T> {
    Object set( T param );
}


class DateDeserializer implements ObjectSerializer {

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type type, int i) {
        SerializeWriter out = serializer.getWriter();
        if (object == null) {
            out.writeNull();
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
        String fn = "datetime";
        Class<?> objClass = object.getClass();
        fn = objClass == java.util.Date.class ? "datetime"
                : objClass == java.sql.Date.class ? "date"
                : objClass == java.sql.Time.class ? "time"
                : fn;
        out.write("\"" +String.format( "%s('%s')" ,fn,sdf.format( object ) ) + "\"");
    }
}
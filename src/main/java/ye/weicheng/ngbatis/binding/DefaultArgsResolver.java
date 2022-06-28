// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.binding;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import ye.weicheng.ngbatis.ArgsResolver;
import ye.weicheng.ngbatis.models.MethodModel;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

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



    public Object customToJSON( Object o ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SerializeConfig parserConfig = new SerializeConfig();
            parserConfig.put( Date.class, new DateDeserializer() );
            parserConfig.put( java.sql.Date.class, new DateDeserializer() );
            parserConfig.put( java.sql.Time.class, new DateDeserializer() );
            String text = JSON.toJSONString(o, parserConfig, SerializerFeature.WriteMapNullValue);
            Map map = objectMapper.readValue(text, Map.class);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
//        return JSON.parseObject(text, Feature.AllowArbitraryCommas);
    }

    private boolean isBaseType( Class clazz ) {
        return  clazz == Character.class ||
                clazz == Byte.class ||
                clazz == Short.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Float.class ||
                clazz == Double.class ||
                clazz == Boolean.class ||
                clazz == String.class;
    }

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
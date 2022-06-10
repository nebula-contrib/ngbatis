// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.binding;

import com.alibaba.fastjson.JSON;
import ye.weicheng.ngbatis.ArgsResolver;
import ye.weicheng.ngbatis.models.MethodModel;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        int len = method.getParameterCount();
        Map<String, Object> result = new HashMap<>();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for( int i = 0 ; i < len ; i ++ ) {
            Annotation[] annotationArgIndex = parameterAnnotations[i];
            int annoLen = annotationArgIndex.length;
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
                    result.put( "_" + i,  JSON.toJSON(args[ i ] ) );
                else
                    result.putAll( (Map<String,Object>)JSON.toJSON( args[i] ) );
            }
        }
        return result;
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

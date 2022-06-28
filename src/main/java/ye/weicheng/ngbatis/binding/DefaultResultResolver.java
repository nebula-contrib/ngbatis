// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.binding;

import com.vesoft.nebula.client.graph.data.ResultSet;
import ye.weicheng.ngbatis.ResultHandler;
import ye.weicheng.ngbatis.ResultResolver;
import ye.weicheng.ngbatis.models.MethodModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ye.weicheng.ngbatis.utils.ReflectUtil;

import java.lang.reflect.Method;

/**
 * 默认的结果集处理入口
 *
 * @author yeweicheng
 * <br>Now is history!
 */
@Component
public class DefaultResultResolver implements ResultResolver {

    private Logger log = LoggerFactory.getLogger( DefaultResultResolver.class );

    @Override
    public Object resolve(MethodModel methodModel, ResultSet result) {
        Class returnType = methodModel.getReturnType();
        Class resultType = methodModel.getResultType();

        if( resultType == null ) resultType = returnType;

        if( returnType == void.class ) return null;

        ResultHandler<Object, Object> handler = ResultHandler.getHandler(
                ReflectUtil.sealingBasicType(returnType),
                ReflectUtil.sealingBasicType(resultType)
        );
        log.debug( "针对java返回类型 {} 与 xml 返回类型 {} 的结果集处理器为：{}", returnType.getName(), resultType.getName(), handler );

        if( handler == null ) {
            return result;
        }
        return handler.handle(returnType, result, resultType );
    }


}

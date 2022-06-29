// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.handler;

import com.vesoft.nebula.client.graph.data.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ye.weicheng.ngbatis.ResultHandler;
import ye.weicheng.ngbatis.exception.QueryException;
import ye.weicheng.ngbatis.exception.ResultHandleException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static ye.weicheng.ngbatis.utils.ReflectUtil.isBasicType;

/**
 * 结果集处理器的基类，主要作用有：
 * <ul>
 *     <li>用于做基本的类型校验</li>
 *     <li>调用不同数据类型下的结果集处理器</li>
 * </ul>
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public abstract class AbstractResultHandler<T, Z> implements ResultHandler<T, Z> {

    private Logger log = LoggerFactory.getLogger( AbstractResultHandler.class );
    protected boolean isReturnTypeInterface( Class returnType ) {
        return returnType.isInterface();
    }

    public T handle(Class returnType, ResultSet result, Class resultType) {
        if( returnType == ResultSet.class ) {
            return (T)result;
        }
        if(!result.isSucceeded()) {
            throw new QueryException( result.getErrorMessage() );
        }

        T newResult = (T) newInstance( returnType, resultType );

        if(!(newResult instanceof Collection) && result.rowsSize() > 1) {
            throw new ResultHandleException("返回值要求只有一个值，但却出现了一行以上记录。");
        }

        List<String> columnNames = result.getColumnNames();
        if( isBasicType(returnType) && columnNames.size() > 1) {
            throw new ResultHandleException("接口返回类型为基本类型，结果集却存在多个列。"+columnNames);
        }

        try {
            return handle(newResult, result, resultType);
        } catch (Exception e) {
            throw new ResultHandleException( e );
        }
    }


    protected T newInstance(Class<T> returnType, Class resultType) {
        if( isReturnTypeInterface( returnType ) ) {
            return this.defaultInstance( returnType );
        } else {
            try {
                Object t = resultType.newInstance();
                return (T) t;
            } catch (InstantiationException | IllegalAccessException e) {
                log.error( "泛型第二个参数" + resultType.getName()+ "的类型，不支持实例化。" );
                return null;
            }
        }
    }

    protected T defaultInstance( Class returnType ) {
        Object o = returnType == List.class ? new ArrayList()
                : returnType == Set.class ? new  HashSet()
                : returnType == Queue.class ? new ConcurrentLinkedQueue()
                : returnType == Map.class ? new HashMap<>()
                : null;
        return (T)o;
    }

    public AbstractResultHandler() {
        Type[] typeParameters = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        if( typeParameters != null && typeParameters.length == 2 ) {
            try {
                addHandler(
                        Class.forName(typeParameters[0].getTypeName()),
                        Class.forName(typeParameters[1].getTypeName())
                );
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    void addHandler( Class returnType, Class resultType ) {
        ResultHandler bean = this;
        DIFF_TYPE_RESULT_HANDLER.put(returnType, resultType, bean);
        HANDLERS.putIfAbsent(returnType, bean);
    }

    protected String getLastName( String name ) {
        String[] split = name.split("\\.");
        if( split.length == 1 ) {
            return split[0];
        } else {
            return split[ split.length - 1];
        }
    }

    protected String getLastLabel( String[] labels ) {
        if(labels != null) {
            int length = labels.length;
            if (length > 0) {
                return labels[ length - 1 ];
            }
        }
        return  null;
    }
}

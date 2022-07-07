// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.proxy;

import com.alibaba.fastjson.JSON;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.net.Session;
import ye.weicheng.ngbatis.ArgNameFormatter;
import ye.weicheng.ngbatis.Env;
import ye.weicheng.ngbatis.ResultResolver;
import ye.weicheng.ngbatis.annotations.UseKeyArgReplace;
import ye.weicheng.ngbatis.config.ParseCfgProps;
import ye.weicheng.ngbatis.exception.QueryException;
import ye.weicheng.ngbatis.models.ClassModel;
import ye.weicheng.ngbatis.models.MapperContext;
import ye.weicheng.ngbatis.models.MethodModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ye.weicheng.ngbatis.utils.Page;
import ye.weicheng.ngbatis.utils.ReflectUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ye.weicheng.ngbatis.models.ClassModel.PROXY_SUFFIX;


/**
 * 被动态代理类所调用。用于实际的数据库访问并调用结果集处理方法
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class MapperProxy {

    private static Logger log = LoggerFactory.getLogger( MapperProxy.class );
    @Autowired
    private ParseCfgProps props;

    public static Env ENV;

    private ClassModel classModel;

    private Map<String, MethodModel> methodCache = new HashMap<>();


    public MapperProxy(ClassModel classModel) {
        this.classModel = classModel;
        methods( classModel );
    }

    private void methods( ClassModel classModel ) {
        methodCache.clear();
//        Method[] declaredMethods = classModel.getNamespace().getDeclaredMethods();
        Map<String, MethodModel> methods = classModel.getMethods();
        methodCache.putAll( methods );
//        for( Method method : declaredMethods ) {
//            methodCache.put( method, methods.get( method.getName() ) );
//        }
    };

    /**
     * 提供给代理类所调用
     * @param className
     * @param methodName
     * @param args
     * @return
     */
    public static Object invoke(String className, String methodName, Object... args ) {
        MapperContext mapperContext = ENV.getMapperContext();
        String proxyClassName = className + PROXY_SUFFIX;
        ClassModel classModel = mapperContext.getInterfaces().get(proxyClassName);
        Method method = null;
        if( mapperContext.isResourceRefresh()) {
            try {
                Map<String, ClassModel> classModelMap = classModel.getResourceLoader().parseClassModel(classModel.getResource());
                classModel = classModelMap.get(proxyClassName);
                method = classModel.getMethod(methodName).getMethod();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            method = classModel.getMethod(methodName).getMethod();
        }
        return pageSupport( classModel, method, args );
    }

    private static Object pageSupport(ClassModel classModel, Method method, Object[] args) {
        int pageParamIndex = ReflectUtil.containsType(method, Page.class);

        MapperProxy mapperProxy = new MapperProxy(classModel);
        if( pageParamIndex < 0 ) {
            return mapperProxy.invoke( method, args );
        }

        String countMethodName = method.getName() + "$Count";
        String pageMethodName = method.getName() + "$Page";

        Long count = (Long)mapperProxy.invoke( classModel.getMethods().get(countMethodName), args );
        List rows = (List)mapperProxy.invoke( classModel.getMethods().get(pageMethodName), args );

        Page page = (Page)args[pageParamIndex];
        page.setTotal( count );
        page.setRows( rows );
        return rows;
    }

    /**
     * 提供给基类所调用
     *
     * @param methodModel
     * @param args
     * @return
     */
    public static Object invoke(MethodModel methodModel, Object... args) {
        Method method = methodModel.getMethod();
        ResultSet query = null;
        // 参数格式转换
        long step0 = System.currentTimeMillis();
        Map<String, Object> argMap = ENV.getArgsResolver().resolve( methodModel, args );
        // beetl 渲染模板
        String textTpl = methodModel.getText();
        String nGQL = ENV.getTextResolver().resolve( textTpl, argMap );
        Map<String, Object> params = null;
        if( method != null &&  method.isAnnotationPresent( UseKeyArgReplace.class ) ) {
            ArgNameFormatter.CqlAndArgs format = ENV.getArgNameFormatter().format(nGQL, argMap);
            nGQL = format.getCql();
            params = format.getArgs();
        } else {
            params = argMap;
        }

        long step1 = System.currentTimeMillis();
        query = executeWithParameter( nGQL, params );

        long step2 = System.currentTimeMillis();
        if (!query.isSucceeded()) {
            throw new QueryException( "数据查询失败：" + query.getErrorMessage() );
        }

        if(methodModel.getResultType() == ResultSet.class) return query;

        ResultResolver resultResolver = ENV.getResultResolver();
        Object resolve = resultResolver.resolve(methodModel, query);
        long step3 = System.currentTimeMillis();

        log.debug( "nGql make up costs {}ms, query costs {}ms, result handle costs {}ms ", step1 - step0, step2 - step1, step3 - step2 );
        return resolve;
    }

    public Object invoke(Method method, Object... args)  {
        MethodModel methodModel = methodCache.get(method.getName());
        methodModel.setMethod( method );

        return invoke(methodModel, args);
    }

    public static  ResultSet executeWithParameter( String nGQL, Map<String, Object> params )  {
        Session session = null;
        ResultSet result = null;
        String proxyClass = null;
        String proxyMethod = null;
        try {
            if(log.isDebugEnabled()) {
                StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[6];
                proxyClass = stackTraceElement.getClassName();
                proxyMethod = stackTraceElement.getMethodName();
            }

            nGQL = "USE " + ENV.getSpace()+";\n\t\t" + nGQL.trim();
            session = ENV.openSession();
            result = session.executeWithParameter( nGQL, params );
            if( result.isSucceeded() ) {
                return result;
            } else {
                throw new QueryException( " 数据查询失败" + result.getErrorMessage() );
            }
        } catch (IOErrorException e) {
            throw new QueryException(  "数据查询失败："  + e.getMessage() );
        } finally {
            log.debug("\n\t- proxyMethod: {}#{} \n\t- nGql：{} \n\t - params: {}\n\t - result：{}", proxyClass, proxyMethod, nGQL, params, result);
//            if (session != null ) session.release();
        }
    }

    public static Logger getLog() {
        return log;
    }

    public static void setLog(Logger log) {
        MapperProxy.log = log;
    }

    public ParseCfgProps getProps() {
        return props;
    }

    public void setProps(ParseCfgProps props) {
        this.props = props;
    }

    public ClassModel getClassModel() {
        return classModel;
    }

    public void setClassModel(ClassModel classModel) {
        this.classModel = classModel;
    }

    public Map<String, MethodModel> getMethodCache() {
        return methodCache;
    }

    public void setMethodCache(Map<String, MethodModel> methodCache) {
        this.methodCache = methodCache;
    }
}

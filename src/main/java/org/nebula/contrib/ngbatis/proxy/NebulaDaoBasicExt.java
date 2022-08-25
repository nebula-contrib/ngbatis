package org.nebula.contrib.ngbatis.proxy;

// Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.nebula.contrib.ngbatis.models.MethodModel;
import org.nebula.contrib.ngbatis.utils.StringUtil;

import javax.persistence.Table;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.isCurrentTypeOrParentType;

/**
 * @author yeweicheng
 * @since 2022-06-14 4:25
 * <br>Now is history!
 */
public class NebulaDaoBasicExt {

    /**
     * 根据节点实体类型，获取数据库中的节点类型名
     * @param entityType 节点实体类类型
     * @return 数据库中的 节点类型名
     */
    public static String vertexName( Class<?> entityType ) {
        Table tableAnno = entityType.getAnnotation( Table.class );
        return tableAnno != null
                ? tableAnno.name()
                : StringUtil.xX2x_x(entityType.getSimpleName());
    }

    /**
     * 根据关系的实体类型，获取数据库中关系的类型名
     * @param edgeType 关系的实体类型
     * @return 数据库中的 关系类型名
     */
    public static String edgeName( Class<?> edgeType ) {
        return vertexName( edgeType );
    }


   static  Map<Class<?>, String> valueFormat = new HashMap<Class<?>, String>() {{
        put( String.class , "'%s'");
    }};

    /**
     * 值格式化，将实体的属性转换成占位符字符串
     *
     * @param value 实体属性值
     * @param name 属性名
     * @return 占位符自负串
     */
    static Object valueFormat( Object value, Object name ) {
        Class<?> fieldType = value.getClass();
        return valueFormat.containsKey(fieldType) ?
                String.format( valueFormat.get(fieldType) , name )
                : name;
    }

    /**
     * 主键格式化：将主键属性转换成占位符或者实际值形式的字符串
     * @param value 主键值
     * @param name 属性名
     * @param asStmt 是否使用住形式。 即： statement 与 prepare-statement 之间的选择
     * @return 用于给主键值提供占位的占位字符串
     */
    static String keyFormat( Object value, String name, boolean asStmt ) {
        String format = asStmt ? "${ nvl( %s, 'null' ) }" : "$%s";
        return valueFormat(value, String.format( format, name ) ).toString();
    }

    /**
     * 主键格式化：将主键属性转换成占位符或者实际值形式的字符串
     * @param value 主键值
     * @param name 属性名
     * @param asStmt 是否使用住形式。 即： statement 与 prepare-statement 之间的选择
     * @param prefix 参数前缀。如果输入dao接口的参数是对象类型，则需要使用前缀来拼接出实际的占位参数符
     * @return 主键占位符
     */
    static  String keyFormat( Object value, String name, boolean asStmt, String prefix ) {
        if ( value == null ) return "null";
        if( isNotEmpty(prefix) ) {
            String format = asStmt ? "${ nvl( %s.%s, 'null' ) }" : "$%s.%s";
            return valueFormat(value, String.format( format, prefix, name ) ).toString();
        }
        return keyFormat( value, name, asStmt );
    }

    /**
     * 根据dao接口类型，通过它的泛型，取得其管理的实体类型与主键类型
     * @param currentType 继承了 NebulaDaoBasic 并且声明了泛型T、ID的类，
     * @return 两个元素的Class数组，第一个元素是 实体类型，第二个元素是 主键类型
     */
    public static Class<?>[] entityTypeAndIdType(Class<?> currentType) {
        Class<?>[] result = null;
        Type[] genericInterfaces = currentType.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if( isCurrentTypeOrParentType( genericInterface.getClass(),  ParameterizedType.class ) ) {
                Type[] actualTypeArguments = ((ParameterizedType) genericInterface).getActualTypeArguments();
                result = new Class<?>[] {
                        (Class<?>) actualTypeArguments[0], // T {@link NebulaDaoBasic }
                        (Class<?>) actualTypeArguments[1]  // ID {@link NebulaDaoBasic }
                };
            } else if ( genericInterface instanceof Class ){
                result = entityTypeAndIdType( (Class)genericInterface );
            }
        }
        return result;
    }

    /**
     * 根据dao接口类型，通过它的泛型，取得其管理的实体类型
     * @param currentType 继承了 NebulaDaoBasic 并且声明了泛型T、ID的类，
     * @return 实体类型
     */
    public static Class<?> entityType( Class<?> currentType ) {
        Class<?>[] entityTypeAndIdType = entityTypeAndIdType(currentType);
        if( entityTypeAndIdType != null && entityTypeAndIdType.length > 0) {
            return entityTypeAndIdType[0];
        }
        return null;
    }

    /**
     * 只能由 NebulaDaoBasic 调用，用于获取当前 dao 所管控的实体类
     */
    public static Class<?> entityType() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        Class<?> currentType = getClassFromStack(stackTraceElement);
        return entityType(currentType);
    }

    /**
     * <strong>基类访问数据库的调用入口。</strong><br>
     * 调用入口与 xml 形式的动态代理类一致，均使用了
     * {@link MapperProxy#invoke(org.nebula.contrib.ngbatis.models.MethodModel, java.lang.Object...) MapperProxy.proxy}
     *
     * @param currentType 被动态代理的 dao，NebulaDaoBasic 子类
     * @param returnType 返回值类型
     * @param nGQL 查询脚本（可带占位符的模板）
     * @param argTypes 接口参数值类型
     * @param args 接口参数
     * @return 对结果集进行处理后的 java对象
     */
    public static Object proxy (Class<?> currentType, Class<?> returnType, String nGQL, Class<?>[] argTypes, Object ... args) {
        Method method = null;
        try {
            String methodName = getMethodName();
            method = currentType.getMethod( methodName, argTypes );
        } catch (NoSuchMethodException ignored) {}

        MethodModel methodModel = new MethodModel();
        methodModel.setMethod( method );
        methodModel.setResultType( returnType );
        methodModel.setText( nGQL );
        return MapperProxy.invoke( methodModel, args );
    }


    /**
     * 从基类对应的 xml 中获取 数据库执行脚本，（xml 默认在 resources/NebulaDaoBasic.xml）
     * @return 数据库执行脚本
     */
    public static String getCqlTpl() {
        Map<String, String> daoBasicTpl = MapperProxy.ENV.getMapperContext().getDaoBasicTpl();
        return daoBasicTpl.get(getMethodName());
    }

    public static Class<?> getClassFromStack( StackTraceElement stackTraceElement ) {
        String className = stackTraceElement.getClassName();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException ignored) {} // 从方法栈中获得的 className 不会发生此异常
        return clazz;
    }

    public static MethodModel getMethodModel() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        MethodModel methodModel = new MethodModel();
        String methodName = stackTraceElement.getMethodName();
        Class<?>  dao = getClassFromStack( stackTraceElement );

        assert dao != null;
        Method[] methods = dao.getMethods();
        Method method = null;
        for (Method m : methods ) {
            if( methodName.equals( m.getName() ) ) {
                method = m;
                break;
            }
        }
        assert method != null;
        methodModel.setMethod( method );
        methodModel.setId( methodName );
        methodModel.setReturnType( method.getReturnType() );
        Map<String, String> daoBasicTpl = MapperProxy.ENV.getMapperContext().getDaoBasicTpl();
        methodModel.setText( daoBasicTpl.get( methodName ) );
        return methodModel;
    }

    public static void main(String[] args) {
        String format = String.format("%s", null);
        List<String> strings = Arrays.asList("dd", format, "ees");
        System.out.println( String.join( ",", strings ));
    }

    /**
     * 获取正在被执行的 dao 方法名
     * @return dao方法名
     */
    public static String getMethodName() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        return stackTraceElement.getMethodName();
    }
}

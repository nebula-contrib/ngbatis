// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package org.nebula.contrib.ngbatis.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import org.nebula.contrib.ngbatis.PkGenerator;
import org.nebula.contrib.ngbatis.exception.ParseException;
import org.nebula.contrib.ngbatis.models.MethodModel;
import org.nebula.contrib.ngbatis.utils.ReflectUtil;
import org.nebula.contrib.ngbatis.utils.StringUtil;

import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getAllColumnFields;

/**
 * @author yeweicheng
 * @since 2022-06-14 4:25
 * <br>Now is history!
 */
public class NebulaDaoBasicExt {

    private static Logger log = LoggerFactory.getLogger( NebulaDaoBasicExt.class );

    public static class KV {
        public final List<String> columns = new ArrayList<>();
        public final List<String> valueNames = new ArrayList<>();
    }

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

    /**
     * 对实体对象设置 主键值（通过主键生成策略）
     *
     * @param record 实体类
     * @param pkField 主键属性
     * @param tagName 数据库中的模式名（节点类型与关系类型 名称）
     * @return 主键值
     */
    public static Object setId(Object record, Field pkField, String tagName) {
        try {
            PkGenerator pkGenerator = MapperProxy.ENV.getPkGenerator();
            Object id = ReflectUtil.getValue( record, pkField );
            if(id == null && pkGenerator != null) {
                id = pkGenerator.generate( tagName, pkField.getType() );
                ReflectUtil.setValue( record, pkField, id );
            }
            return id;
        } catch (IllegalAccessException e) {
            log.error( e.getMessage() );
            return null;
        }
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
            if( genericInterface.getClass() == ParameterizedTypeImpl.class ) {
                Type[] actualTypeArguments = ((ParameterizedTypeImpl) genericInterface).getActualTypeArguments();
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
     * 读取参数，并形成非空的键值对
     *
     * @param record 实体参数
     * @return 参数多个属性的键值对，由两个集合构成，以相同下标作为对应的依据
     */
    public static KV notNullFields( Object record ) {
        return notNullFields( record, null );
    }

    /**
     * 读取参数，并形成非空的键值对
     *
     * @param record 实体参数
     * @param prefix 参数前缀，如该接口是多参数接口，或者 record 位于接口参数内部，则需追加一个前缀，
     *               供参数解析器读取，并与实际值进行对应，从而完成参数填充
     * @return 参数多个属性的键值对，由两个集合构成，以相同下标作为对应的依据
     */
    public static KV notNullFields( Object record, String prefix ) {
        if( record == null ) return new KV();
        Field[] fields = getAllColumnFields( record.getClass() );
        return recordToKV(record, fields, true, prefix);
    }

    /**
     * 读取参数，并形成全属性的键值对
     *
     * @param record 实体参数
     * @return 参数多个属性的键值对，由两个集合构成，以相同下标作为对应的依据
     */
    public static KV allFields( Object record ) {
        return allFields( record, null );
    }

    /**
     * 读取参数，并形成全属性的键值对
     *
     * @param record 实体参数
     * @param prefix 参数前缀，如该接口是多参数接口，或者 record 位于接口参数内部，则需追加一个前缀，
     *               供参数解析器读取，并与实际值进行对应，从而完成参数填充
     * @return 参数多个属性的键值对，由两个集合构成，以相同下标作为对应的依据
     */
    public static KV allFields( Object record, String prefix ) {
        Field[] fields = record.getClass().getDeclaredFields();
        return recordToKV( record,  fields, false, prefix );
    }

    /**
     * 根据实体类类型，获取其带有 @Id 注解（有且仅有一个）的属性。
     * @param type 实体类型
     * @return 节点的主键属性
     */
    public static Field getPkField( Class<?> type ) {
        Field[] declaredFields = type.getDeclaredFields();
        return getPkField( declaredFields, type );
    }

    /**
     * 根据关系实体类型，获取其带有 @Id 注解（可有可无）的属性。
     * @param type 实体类型
     * @return 关系的主键属性
     */
    public static Field getRankField( Class<?> type ) {
        Field[] declaredFields = type.getDeclaredFields();
        return getPkField( declaredFields, type, false );
    }

    /**
     * 传入多个属性对象，获取其中带有 @Id 注解（有且仅有一个）的属性
     * @param fields 属性数组。
     * @param type 属性数组归属的类
     * @return 众多属性中，带 @Id 注解的属性（唯一）
     */
    public static Field getPkField(Field[] fields, Class<?> type) {
        return getPkField( fields, type, true );
    }

    /**
     *
     * @param fields 属性数组。
     * @param type 属性数组归属的类
     * @param canNotNull 是否不为空。
     *                   <ol>
     *                      <li>为 true 时：用于获取vertex类型主键，因数据库vertex单值主键的要求</li>
     *                      <li>为 false 时：用于获取 edge类型主键用，因数据库edge中，rank 值的要求为 可以有可以没有。</li>
     *                   </ol>
     * @return 主键属性
     */
   public static Field getPkField(Field[] fields, Class<?> type, boolean canNotNull ) {
        Field pkField = null;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                pkField = field;
            }
        }
        if (canNotNull && pkField == null) {
            throw new ParseException( String.format( "%s 必须有一个属性用 @Id 注解。（javax.persistence.Id）", type ));
        }
        return pkField;
    }

    /**
     * 从基类对应的 xml 中获取 数据库执行脚本，（xml 默认在 resources/NebulaDaoBasic.xml）
     * @return 数据库执行脚本
     */
    public static String getCqlTpl() {
        Map<String, String> daoBasicTpl = MapperProxy.ENV.getMapperContext().getDaoBasicTpl();
        return daoBasicTpl.get(getMethodName());
    }

    /**
     * 对输入接口的参数进行前置处理，转换成 {@link KV KV} 对象，为参数替换做准备
     *
     * @param record 待参数化对象
     * @param fields 对象属性数组
     * @param selective 是否排除空值
     * @param prefix 参数前缀
     * @return 属性占位符与属性值的键值对（双集合，相同下标成对）
     */
    public static KV recordToKV(Object record, Field[] fields, boolean selective, String prefix) {
        KV kv = new KV();
        for (Field field: fields) {
            String name = null;
            if( selective ) {
                Object value = ReflectUtil.getValue(record, field);
                if( value != null ) {
                    name = field.getName();
                }
            } else {
                name = field.getName();
            }
            if( name != null ) {
                kv.columns.add( name );
                // FIXME 使用 stmt 的方式，将实际值写入 nGQL 当中。
                //  在找到 executeWithParameter 通过参数替换的方法之后修改成 pstmt 的形式
                Object o = keyFormat( ReflectUtil.getValue( record, field ), name, true, prefix);
                kv.valueNames.add( String.valueOf( o ) );
            }
        }
        return kv;
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

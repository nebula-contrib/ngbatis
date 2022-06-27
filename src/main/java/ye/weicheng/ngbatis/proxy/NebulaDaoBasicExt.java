// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.proxy;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import ye.weicheng.ngbatis.PkGenerator;
import ye.weicheng.ngbatis.exception.ParseException;
import ye.weicheng.ngbatis.models.MethodModel;
import ye.weicheng.ngbatis.utils.ReflectUtil;
import ye.weicheng.ngbatis.utils.StringUtil;

import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

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


    public static String vertexName( Class<?> entityType ) {
        Table tableAnno = entityType.getAnnotation( Table.class );
        String x_x = StringUtil.xX2x_x(entityType.getName());
        return tableAnno == null ? x_x : tableAnno.name();
    }

    public static String edgeName( Class<?> edgeType ) {
        return vertexName( edgeType );
    }

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

    static Object valueFormat( Field field, Object name ) {
        Class<?> fieldType = field.getType();
        return valueFormat.containsKey(fieldType) ?
                String.format( valueFormat.get(fieldType) , name )
                : name;
    }

    static String keyFormat( Field field, String name, boolean asStmt ) {
        String format = asStmt ? "${ nvl( %s, 'null' ) }" : "$%s";
        return valueFormat(field, String.format( format, name ) ).toString();
    }

    static  String keyFormat( Field field, String name, boolean asStmt, String prefix ) {
        if( isNotEmpty(prefix) ) {
            String format = asStmt ? "${ nvl( %s.%s, 'null' ) }" : "$%s.%s";
            return valueFormat(field, String.format( format, prefix, name ) ).toString();
        }
        return keyFormat( field, name, asStmt );
    }

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

    public static KV notNullFields( Object record ) {
        return notNullFields( record, null );
    }

    public static KV notNullFields( Object record, String prefix ) {
        Field[] fields = record.getClass().getDeclaredFields();
        return recordToKV(record, fields, true, prefix);
    }

    public static KV allFields( Object record ) {
        return allFields( record, null );
    }
    public static KV allFields( Object record, String prefix ) {
        Field[] fields = record.getClass().getDeclaredFields();
        return recordToKV( record,  fields, false, prefix );
    }

    public static Field getPkField( Class<?> type ) {
        Field[] declaredFields = type.getDeclaredFields();
        return getPkField( declaredFields, type );
    }

    public static Field getPkField(Field[] fields, Class<?> type ) {
        Field pkField = null;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                pkField = field;
            }
        }
        if (pkField == null) {
            throw new ParseException( String.format( "%s 必须有一个属性用 @Id 注解。（javax.persistence.Id）", type ));
        }
        return pkField;
    }

    public static String getCqlTpl() {
        Map<String, String> daoBasicTpl = MapperProxy.ENV.getMapperContext().getDaoBasicTpl();
        return daoBasicTpl.get(getMethodName());
    }

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
                Object o = keyFormat( field, name, true, prefix);
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

    public static String getMethodName() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        return stackTraceElement.getMethodName();
    }
}

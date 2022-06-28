// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.utils;

import ye.weicheng.ngbatis.exception.ParseException;
import ye.weicheng.ngbatis.models.MethodModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.util.ObjectUtils.nullSafeEquals;

/**
 * 反射工具类
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class ReflectUtil {

    public static List<Class<?>> NUMBER_TYPES = Arrays.asList(
            int.class, Integer.class,
            long.class, Long.class,
            float.class, Float.class,
            double.class, Double.class,
            byte.class, Byte.class,
            short.class, Short.class
    );

    public static void setValue(Object o, String prop, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = o.getClass().getDeclaredField(prop);
        setValue( o, declaredField, value );
    }

    public static void setValue( Object o, Field field, Object value ) throws IllegalAccessException {
        if ( NUMBER_TYPES.contains( field.getType() )) {
            value = castNumber( (Number) value, field.getType() );
        }
        field.setAccessible( true );
        field.set( o, value );
        field.setAccessible( false );
    }


    public static Number castNumber(Number n, Class resultType) {
        if( n == null) return null;
        return (resultType == Integer.class || resultType == int.class) ? n.intValue()
                : (resultType == Long.class || resultType == long.class) ? n.longValue()
                : (resultType == Float.class || resultType == float.class) ? n.floatValue()
                : (resultType == Double.class || resultType == double.class) ? n.doubleValue()
                : (resultType == Byte.class || resultType == byte.class) ? n.byteValue()
                : (resultType == Short.class || resultType == short.class) ? n.shortValue()
                : n;
    }

    public static Object getValue(Object o, Field field) {
        try {
            field.setAccessible( true );
            Object value = field.get(o);
            field.setAccessible( false );
            return value;
        } catch (IllegalAccessException e) {
            throw new ParseException( e.getMessage() );
        }
    }

    public static String getMethodSignature( MethodModel methodModel ) {
        StringBuilder builder = new StringBuilder( "(");
        Method method = methodModel.getMethod();
        Class<?> returnType;
        Class<?>[] parameterTypes ;
        if ( method == null) {
            returnType = methodModel.getReturnType();
            parameterTypes =  methodModel.getParameterTypes();
        } else {
            returnType = method.getReturnType();
            parameterTypes = method.getParameterTypes();
        }

        int len = parameterTypes.length;
        for(int i = 0; i < len ; i ++ ) {
            Class<?> parameterType = parameterTypes[i];
            builder.append( insnType( parameterType ));
        }
        String canonicalName = returnType.getCanonicalName();
        builder.append( ")");
        if( returnType == void.class ) {
            builder.append( "V");
        } else if( returnType == int.class ) {
            builder.append( "I");
        } else {
            builder.append( "L" );
            builder.append( canonicalName.replace( ".", "/") );
            builder.append( ";");
        }
        String string = builder.toString();
        return string;
    }

    public static int containsType( Method method, Class<?> parameterType ) {
        List<Class<?>> classes = Arrays.asList(method.getParameterTypes());
        return classes.indexOf( parameterType );
    }

    public final static Set<Class<?>> NEED_SEALING_TYPES = new HashSet<Class<?>>() {{
        add( short.class );
        add( int.class );
        add( long.class );
        add( double.class );
        add( float.class );
        add( byte.class );
        add( char.class );
        add( boolean.class );
    }};


    public static Class<?> sealingBasicType(Class<?> returnType) {
        return returnType == short.class ? Short.class
                : returnType == int.class ? Integer.class
                : returnType == long.class ? Long.class
                : returnType == double.class ? Double.class
                : returnType == float.class ? Float.class
                : returnType == byte.class ? Byte.class
                : returnType == char.class ? Character.class
                : returnType == boolean.class ? Boolean.class
                : returnType;
    }

    public static String insnType(Class<?> type ) {
        StringBuilder builder = new StringBuilder();
        if( type == int.class ) {
            builder.append( "I" );
        } else if( type == long.class ) {
            builder.append( "J" );
        } else if( type == void.class ) {
            builder.append( "V" );
        } else {
            builder.append("L");
            String canonicalName = type.getCanonicalName().replace( ".", "/");
            builder.append( canonicalName );
            builder.append( ";" );
        }
        return builder.toString();
    }

    public static Method getNameUniqueMethod( Class clazz, String methodName ) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for( Method method : declaredMethods ) {
            if( nullSafeEquals( method.getName(), methodName ) ) {
//                Class<?> returnType = method.getReturnType();
//                returnType = sealingBasicType(returnType);
//                try {
//                    setValue(method, "returnType", returnType);
//                } catch (NoSuchFieldException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
                return method;
            }
        }
        return null;
    }

    public final static List<Class> CLASSES;
    static {
        CLASSES = Arrays.asList( String.class,
                Character.class, char.class,
                Float.class, float.class,
                Double.class, double.class,
                Byte.class, byte.class,
                Short.class, short.class,
                Integer.class, int.class,
                Long.class, long.class,
                Boolean.class, boolean.class
            );
    }

    public static boolean isBasicType( Class clazz ) {
        return CLASSES.contains(clazz);
    }

    public static Class<?> fieldType(Object o, String fieldName) {
        try {
            Field field = o.getClass().getDeclaredField(fieldName);
            return field.getType();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}

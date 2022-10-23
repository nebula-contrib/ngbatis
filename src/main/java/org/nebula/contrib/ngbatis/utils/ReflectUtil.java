package org.nebula.contrib.ngbatis.utils;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.springframework.util.ObjectUtils.nullSafeEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import org.nebula.contrib.ngbatis.exception.ParseException;
import org.nebula.contrib.ngbatis.models.MethodModel;

/**
 * <p>反射工具类。</p>
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public abstract class ReflectUtil {

  public static final Set<Class<?>> NEED_SEALING_TYPES = new HashSet<Class<?>>() {{
      add(short.class);
      add(int.class);
      add(long.class);
      add(double.class);
      add(float.class);
      add(byte.class);
      add(char.class);
      add(boolean.class);
    }};
  public static final List<Class<?>> CLASSES;
  public static final List<Class<?>> NUMBER_TYPES = Arrays.asList(
      int.class, Integer.class,
      long.class, Long.class,
      float.class, Float.class,
      double.class, Double.class,
      byte.class, Byte.class,
      short.class, Short.class
  );

  static {
    CLASSES = Arrays.asList(String.class,
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

  public static void setValue(Object o, String prop, Object value)
      throws NoSuchFieldException, IllegalAccessException {
    Field[] allColumnFields = getAllColumnFields(o.getClass());
    Field declaredField = null;
    for (Field columnField : allColumnFields) {
      if (getNameByColumn(columnField).equals(prop)) {
        declaredField = columnField;
        break;
      }
    }
    if (declaredField == null) {
      throw new NoSuchFieldException(prop);
    }
    //Field declaredField = o.getClass().getDeclaredField(prop);
    setValue(o, declaredField, value);
  }

  /**
   * <p>反射设值。将value设置到o的field属性中。</p>
   * @param o 待设值对象
   * @param field 待设值属性
   * @param value 值
   * @throws IllegalAccessException o 中可能不存在 field 属性
   */
  public static void setValue(Object o, Field field, Object value) throws IllegalAccessException {
    if (NUMBER_TYPES.contains(field.getType())) {
      value = castNumber((Number) value, field.getType());
    }
    boolean accessible = field.isAccessible();
    if (accessible) {
      field.set(o, value);
    } else {
      field.setAccessible(true);
      field.set(o, value);
      field.setAccessible(false);
    }
  }

  /**
   * get Column.class name field value, to replace the original field
   *
   * @param field  对象属性
   * @return 获取Column.class name字段值，以替换原始字段
   */
  public static String getNameByColumn(Field field) {
    Column columnAnno = field.getAnnotation(Column.class);
    if (columnAnno != null && columnAnno.name().length() > 0) {
      return columnAnno.name();
    }
    return field.getName();
  }

  /**
   * <p>强转数字型。</p>
   * @param n 数字
   * @param resultType 目标的数字类型
   * @return 数字值
   */
  public static Number castNumber(Number n, Class resultType) {
    if (n == null) {
      return null;
    }
    return (resultType == Integer.class || resultType == int.class) ? n.intValue()
      : (resultType == Long.class || resultType == long.class) ? n.longValue()
        : (resultType == Float.class || resultType == float.class) ? n.floatValue()
          : (resultType == Double.class || resultType == double.class) ? n.doubleValue()
            : (resultType == Byte.class || resultType == byte.class) ? n.byteValue()
              : (resultType == Short.class || resultType == short.class) ? n.shortValue()
                : n;
  }

  /**
   * <p>反射取值。</p>
   * @param o 对象
   * @param field 属性
   * @return 属性值
   */
  public static Object getValue(Object o, Field field) {
    try {
      boolean accessible = field.isAccessible();
      if (accessible) {
        return field.get(o);
      } else {
        field.setAccessible(true);
        Object value = field.get(o);
        field.setAccessible(false);
        return value;
      }
    } catch (IllegalAccessException e) {
      throw new ParseException(e.getMessage());
    }
  }

  /**
   * <p>根据方法模型，生成字节码方法签名。</p>
   * @param methodModel 接口方法模型
   * @return 方法签名
   */
  public static String getMethodSignature(MethodModel methodModel) {
    StringBuilder builder = new StringBuilder("(");
    Method method = methodModel.getMethod();
    Class<?> returnType;
    Class<?>[] parameterTypes;
    if (method == null) {
      returnType = methodModel.getReturnType();
      parameterTypes = methodModel.getParameterTypes();
    } else {
      returnType = method.getReturnType();
      parameterTypes = method.getParameterTypes();
    }

    int len = parameterTypes.length;
    for (int i = 0; i < len; i++) {
      Class<?> parameterType = parameterTypes[i];
      builder.append(insnType(parameterType));
    }
    String canonicalName = returnType.getCanonicalName();
    builder.append(")");
    if (returnType == void.class) {
      builder.append("V");
    } else if (returnType == int.class) {
      builder.append("I");
    } else {
      builder.append("L");
      builder.append(canonicalName.replace(".", "/"));
      builder.append(";");
    }
    String string = builder.toString();
    return string;
  }

  public static int containsType(Method method, Class<?> parameterType) {
    List<Class<?>> classes = Arrays.asList(method.getParameterTypes());
    return classes.indexOf(parameterType);
  }

  /**
   * <p>对基本类型进行封箱。</p>
   * @param basicType 基本类型
   * @return 基本类型对应的封装类
   */
  public static Class<?> sealingBasicType(Class<?> basicType) {
    return basicType == short.class ? Short.class
      : basicType == int.class ? Integer.class
        : basicType == long.class ? Long.class
          : basicType == double.class ? Double.class
            : basicType == float.class ? Float.class
              : basicType == byte.class ? Byte.class
                : basicType == char.class ? Character.class
                  : basicType == boolean.class ? Boolean.class
                    : basicType;
  }

  /**
   * <p>将类型转换成 insn 编码。</p>
   * @param type 待转编码的类型
   * @return asm中 的 insn 信息
   */
  public static String insnType(Class<?> type) {
    StringBuilder builder = new StringBuilder();
    if (type == int.class) {
      builder.append("I");
    } else if (type == long.class) {
      builder.append("J");
    } else if (type == void.class) {
      builder.append("V");
    } else {
      builder.append("L");
      String canonicalName = type.getCanonicalName().replace(".", "/");
      builder.append(canonicalName);
      builder.append(";");
    }
    return builder.toString();
  }

  /**
   * <p>根据方法名，获取唯一的方法。</p>
   * @param clazz 类对象
   * @param methodName 方法名
   * @return 方法
   */
  public static Method getNameUniqueMethod(Class clazz, String methodName) {
    Method[] declaredMethods = clazz.getDeclaredMethods();
    for (Method method : declaredMethods) {
      if (nullSafeEquals(method.getName(), methodName)) {
        return method;
      }
    }
    return null;
  }

  public static boolean isBasicType(Class clazz) {
    return CLASSES.contains(clazz);
  }

  /**
   * <p>根据对象与属性名，获取属性类型。</p>
   * @param o 对象
   * @param fieldName 属性名
   * @return 属性类型
   */
  public static Class<?> fieldType(Object o, String fieldName) {
    try {
      Field field = o.getClass().getDeclaredField(fieldName);
      return field.getType();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 判断 parentType 是否是 paramType 或其父类、接口
   * @param paramType 待判断类型-子类
   * @param parentType 待判断类型-父类或接口
   * @return paramType 是否为 parentType 子类或实现类 
   */
  public static boolean isCurrentTypeOrParentType(Class<?> paramType, Class<?> parentType) {
    if (paramType == parentType) {
      return true;
    }
    Set<Class<?>> parentTypes = getParentTypes(paramType);
    return parentTypes.contains(parentType);
  }

  /**
   * <p>根据参数所传的类，获取该类的父类与接口类型集合。</p>
   * @param paramType 想获得父类与接口类型集合的类型。
   * @return 父类与接口类型集合
   */
  public static Set<Class<?>> getParentTypes(Class<?> paramType) {
    if (paramType == null) {
      return new HashSet<>();
    }
    List<Class<?>> interfaces = Arrays.asList(paramType.getInterfaces());
    Set<Class<?>> parents = new HashSet<>(interfaces);

    for (Class<?> anInterface : interfaces) {
      parents.addAll(getParentTypes(anInterface));
    }

    Class<?> superclass = paramType.getSuperclass();
    parents.add(superclass);
    parents.addAll(getParentTypes(superclass));
    return parents;
  }

  /**
   * 实体类获取全部属性，对父类获取 带 @Column 的属性
   *
   * @param clazz 实体类
   * @return 当前类的属性及其父类中，带@Column注解的属性
   */
  public static Field[] getAllColumnFields(Class<?> clazz) {
    Set<Field> fields = new HashSet<>();
    boolean leaf = true;
    do {
      Field[] declaredFields = clazz.getDeclaredFields();
      if (leaf) {
        Set<Field> cols = Arrays.stream(declaredFields)
          .filter(el -> !el.isAnnotationPresent(Transient.class))
          .collect(Collectors.toSet());
        fields.addAll(cols);
      } else {
        for (Field declaredField : declaredFields) {
          if (declaredField.isAnnotationPresent(Column.class)) {
            fields.add(declaredField);
          }
        }
      }
      clazz = clazz.getSuperclass();
      leaf = false;
    } while (clazz != null);
    return fields.toArray(new Field[0]);
  }

  /**
   * 根据实体类类型，获取其带有 @Id 注解（有且仅有一个）的属性。
   *
   * @param type 实体类型
   * @return 节点的主键属性
   */
  public static Field getPkField(Class<?> type) {
    return getPkField(type, true);
  }

  public static Field getPkField(Class<?> type, boolean canNotNull) {
    Field[] declaredFields = getAllColumnFields(type);
    return getPkField(declaredFields, type, canNotNull);
  }

  /**
   * 传入多个属性对象，获取其中带有 @Id 注解（有且仅有一个）的属性
   *
   * @param fields 属性数组。
   * @param type   属性数组归属的类
   * @return 众多属性中，带 @Id 注解的属性（唯一）
   */
  public static Field getPkField(Field[] fields, Class<?> type) {
    return getPkField(fields, type, true);
  }

  /**
   * <p>获取主键（@Id注解）属性。</p>
   * @param fields   属性数组。
   * @param type     属性数组归属的类
   * @param canNotNull 是否不为空。
   *           <ol>
   *            <li>为 true 时：用于获取vertex类型主键，因数据库vertex单值主键的要求</li>
   *            <li>为 false 时：用于获取 edge类型主键用，因数据库edge中，rank 值的要求为 可以有可以没有。</li>
   *           </ol>
   * @return 主键属性
   */
  public static Field getPkField(Field[] fields, Class<?> type, boolean canNotNull) {
    Field pkField = null;
    for (Field field : fields) {
      if (field.isAnnotationPresent(Id.class)) {
        pkField = field;
      }
    }
    if (canNotNull && pkField == null) {
      throw new ParseException(
        String.format("%s 必须有一个属性用 @Id 注解。（javax.persistence.Id）", type));
    }
    return pkField;
  }

}

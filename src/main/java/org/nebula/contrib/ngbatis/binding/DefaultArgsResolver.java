package org.nebula.contrib.ngbatis.binding;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getAllColumnFields;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.isCurrentTypeOrParentType;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.typeArg;

import com.alibaba.fastjson.JSON;
import com.vesoft.nebula.DataSet;
import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.Duration;
import com.vesoft.nebula.Edge;
import com.vesoft.nebula.Geography;
import com.vesoft.nebula.NList;
import com.vesoft.nebula.NMap;
import com.vesoft.nebula.NSet;
import com.vesoft.nebula.Path;
import com.vesoft.nebula.Time;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.Vertex;
import jakarta.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.nebula.contrib.ngbatis.ArgsResolver;
import org.nebula.contrib.ngbatis.models.MethodModel;
import org.nebula.contrib.ngbatis.utils.ReflectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

/**
 * 默认的参数解析器。
 * @author yeweicheng
 * <br>Now is history!
 */
@Component
public class DefaultArgsResolver implements ArgsResolver {
    
  @Autowired(required = false) private List<Setter<?>> setters;
  
  @PostConstruct
  public void init() {
    if (setters == null) {
      return;
    }
    setters.forEach((setter) -> {
      Class<?> typeArg = typeArg(setter, Setter.class, 0);
      LEAF_TYPE_AND_SETTER.put(typeArg, setter);
    });
  }

  public static Map<Class<?>, Setter<?>> LEAF_TYPE_AND_SETTER =
      new HashMap<Class<?>, Setter<?>>() {{
      put(boolean.class, (Setter<Boolean>) Value::bVal);
      put(Boolean.class, (Setter<Boolean>) Value::bVal);
      put(int.class, (Setter<Integer>) Value::iVal);
      put(Integer.class, (Setter<Integer>) Value::iVal);
      put(short.class, (Setter<Short>) Value::iVal);
      put(Short.class, (Setter<Short>) Value::iVal);
      put(byte.class, (Setter<Short>) Value::iVal);
      put(Byte.class, (Setter<Short>) Value::iVal);
      put(long.class, (Setter<Long>) Value::iVal);
      put(Long.class, (Setter<Long>) Value::iVal);
      put(float.class, (Setter<Float>) Value::fVal);
      put(Float.class, (Setter<Float>) Value::fVal);
      put(double.class, (Setter<Double>) Value::fVal);
      put(Double.class, (Setter<Double>) Value::fVal);
      put(byte[].class, (Setter<byte[]>) Value::sVal);
      put(String.class, (Setter<String>) (param) -> Value.sVal(param.getBytes()));
      put(com.vesoft.nebula.Date.class, (Setter<com.vesoft.nebula.Date>) Value::dVal);
      put(Time.class, (Setter<Time>) Value::tVal);
      put(DateTime.class, (Setter<DateTime>) Value::dtVal);
      put(Vertex.class, (Setter<Vertex>) Value::vVal);
      put(Edge.class, (Setter<Edge>) Value::eVal);
      put(Path.class, (Setter<Path>) Value::pVal);
      put(NList.class, (Setter<NList>) Value::lVal);
      put(NMap.class, (Setter<NMap>) Value::mVal);
      put(NSet.class, (Setter<NSet>) Value::uVal);
      put(DataSet.class, (Setter<DataSet>) Value::gVal);
      put(Geography.class, (Setter<Geography>) Value::ggVal);
      put(Duration.class, (Setter<Duration>) Value::duVal);
      put(
        java.time.Duration.class, 
        (java.time.Duration du) -> Value.duVal(
          new Duration()
            .setSeconds(du.getSeconds())
            .setMicroseconds(du.getNano() * 1000)
        )
      );
    }};

  @SuppressWarnings("rawtypes")
  public static Map<Class<?>, Setter> COMPLEX_TYPE_AND_SETTER =
      new LinkedHashMap<Class<?>, Setter>() {{

        put(Collection.class, (Setter<Collection<?>>) (collection) -> {
          List<Object> list = new ArrayList<>();
          collection.forEach(el -> list.add(toNebulaValueType(el)));
          return list;
        });
  
        put(Map.class, (Setter<Map<?, ?>>) (map) -> {
          Map<Object, Object> valueMap = new HashMap<>();
          map.forEach((k, v) -> valueMap.put(k, toNebulaValueType(v)));
          return valueMap;
        });
  
        put(Date.class, (Setter<Date>) (date) -> {
          Calendar calendar = new Calendar.Builder().setInstant(date).build();
          return Value.dtVal(new DateTime(
            Short.parseShort(String.valueOf(calendar.get(Calendar.YEAR))),
            Byte.parseByte(String.valueOf(calendar.get(Calendar.MONTH))),
            Byte.parseByte(String.valueOf(calendar.get(Calendar.DATE))),
            Byte.parseByte(String.valueOf(calendar.get(Calendar.HOUR))),
            Byte.parseByte(String.valueOf(calendar.get(Calendar.MINUTE))),
            Byte.parseByte(String.valueOf(calendar.get(Calendar.SECOND))),
            Short.parseShort(String.valueOf(calendar.get(Calendar.MILLISECOND)))
          ));
        });
  
        put(Object.class, (Setter<Object>) (obj) -> {
          Map<String, Object> pojoFields = new HashMap<>();
          Class<?> paramType = obj.getClass();
          Field[] allFields = getAllColumnFields(paramType, true);
          for (Field declaredField : allFields) {
            Object nebulaValue = toNebulaValueType(
              ReflectUtil.getValue(obj, declaredField),
              declaredField
            );
            pojoFields.put(declaredField.getName(),nebulaValue);
          }
          return pojoFields;
        });
    }};

  /**
   * 将任意java对象转换成nebula-client可以接收的对象值。
   * @param param 任意java对象
   * @param <T> 调用方自定义接收类型
   * @return nebula-client可接收对象
   */
  @SuppressWarnings({"unchecked"})
  public static <T> T toNebulaValueType(Object param) {
    return toNebulaValueType(param, null);
  }

  /**
   * 将任意java对象转换成nebula-client可以接收的对象值。
   * @param param 任意java对象
   * @param field 当待转换值是POJO屬性类型时的属性类型
   * @param <T> 调用方自定义接收类型
   * @return nebula-client可接收对象
   */
  @SuppressWarnings({"unchecked"})
  public static <T> T toNebulaValueType(Object param, Field field) {
    if (param == null) {
      return null;
    }
    Class<?> paramType = param.getClass();
    @SuppressWarnings("rawtypes")
    Setter setter = LEAF_TYPE_AND_SETTER.get(paramType);
    if (setter != null) {
      return (T) setter.set(param, field);
    }
    for (Class<?> parentType : COMPLEX_TYPE_AND_SETTER.keySet()) {
      if (isCurrentTypeOrParentType(paramType, parentType)) {
        return (T) COMPLEX_TYPE_AND_SETTER.get(parentType).set(param);
      }
    }
    return (T) param;
  }

  @Override
  public Map<String, Object> resolveForTemplate(MethodModel methodModel, Object... args) {
    return resolve(true, methodModel, args);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> resolve(MethodModel methodModel, Object... args) {
    return resolve(false, methodModel, args);
  }

  private Map<String, Object> resolve(boolean forTemplate,
      MethodModel methodModel, Object... args) {
    if (args.length == 0) {
      return Collections.emptyMap();
    }
    int len = methodModel.getParameterCount();
    Map<String, Object> result = new LinkedHashMap<>();
    Annotation[][] parameterAnnotations = methodModel.getParameterAnnotations();
    for (int i = 0; i < len; i++) {
      Annotation[] annotationArgIndex = parameterAnnotations[i];
      int annoLen = annotationArgIndex == null ? 0 : annotationArgIndex.length;
      boolean notFoundParamAnno = true;
      for (int j = 0; j < annoLen; j++) {
        if (annotationArgIndex[j] instanceof Param) {
          Param annotationArgIndex1 = (Param) annotationArgIndex[j];
          String key = annotationArgIndex1.value();
          result.put(key, serialize(forTemplate, args[i]));
          notFoundParamAnno = false;
        }
      }
      if (notFoundParamAnno) {
        Class<?> paramClass = args[i].getClass();
        if (isBaseType(paramClass)) {
          result.put("p" + i, serialize(forTemplate, args[i]));
        } else if (args[i] instanceof Collection) {
          result.put("p" + i, serialize(forTemplate, args[i]));
        } else {
          if (len == 1) {
            result = (Map<String, Object>) serialize(forTemplate, args[0]);
          } else {
            result.put("p" + i, serialize(forTemplate, args[i]));
          }
        }
      }
    }
    return result;
  }
  
  private Object serialize(boolean forTemplate, Object o) {
    return forTemplate ? JSON.toJSON(o) : toNebulaValueType(o);
  }

  private boolean isBaseType(Class<?> clazz) {
    return clazz == Character.class || clazz == char.class
      || clazz == Byte.class || clazz == byte.class
      || clazz == Short.class || clazz == short.class
      || clazz == Integer.class || clazz == int.class
      || clazz == Long.class || clazz == long.class
      || clazz == Float.class || clazz == float.class
      || clazz == Double.class || clazz == double.class
      || clazz == Boolean.class || clazz == boolean.class
      || clazz == String.class;
  }

}

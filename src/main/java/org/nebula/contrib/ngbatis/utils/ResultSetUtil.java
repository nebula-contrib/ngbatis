package org.nebula.contrib.ngbatis.utils;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.utils.ReflectUtil.castNumber;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getPkField;

import com.vesoft.nebula.ErrorCode;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.DateTimeWrapper;
import com.vesoft.nebula.client.graph.data.DateWrapper;
import com.vesoft.nebula.client.graph.data.Node;
import com.vesoft.nebula.client.graph.data.Relationship;
import com.vesoft.nebula.client.graph.data.ValueWrapper;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nebula.contrib.ngbatis.exception.ResultHandleException;
import org.nebula.contrib.ngbatis.models.MapperContext;
import org.nebula.contrib.ngbatis.proxy.MapperProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 结果集基础类型处理的工具类
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class ResultSetUtil {

  private static Logger log = LoggerFactory.getLogger(ResultSetUtil.class);

  /**
   * <p>根据nebula graph本身的类型说明，获取对应的 java对象值。</p>
   * @param value nebula graph 类型数据，（结果集的元素）
   * @param <T> 目标结果类型
   * @return
   */
  public static <T> T getValue(ValueWrapper value) {
    try {
      Object o = value.isLong() ? value.asLong()
          : value.isBoolean() ? value.asBoolean()
          : value.isDouble() ? value.asDouble()
            : value.isString() ? value.asString()
              : value.isTime() ? value.asTime()
                : value.isDate() ? transformDate(value.asDate())
                  : value.isDateTime() ? transformDateTime(value.asDateTime())
                    : value.isVertex() ? transformNode(value.asNode())
                      : value.isEdge() ? value.asRelationship()
                        : value.isPath() ? value.asPath()
                          : value.isList() ? transformList(value.asList())
                            : value.isSet() ? transformList(value.asList())
                              : value.isMap() ? transformMap(value.asMap())
                                : null;

      return (T) o;
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 根据 resultType 从 nebula 的数据类型中获取 java 类型数据
   * @param valueWrapper nebula 的数据类型
   * @param resultType 接口返回值类型（类型为集合时，为集合泛型）
   * @param <T> 调用方用来接收结果的类型，即 resultType
   * @return java类型结果
   */
  public static <T> T getValue(ValueWrapper valueWrapper, Class<T> resultType) {
    T value = getValue(valueWrapper);
    if (value instanceof Number) {
      value = (T) castNumber((Number) value, resultType);
    }
    return value;
  }


  private static Object transformDateTime(DateTimeWrapper dateTime) {
    return new GregorianCalendar(
      dateTime.getYear(),
      dateTime.getMonth() - 1,
      dateTime.getDay(),
      dateTime.getHour(),
      dateTime.getMinute(),
      dateTime.getSecond()
    ).getTime();
  }

  private static Object transformDate(DateWrapper date) {
    return new GregorianCalendar(date.getYear() + 1900, date.getMonth() - 1, date.getDay())
      .getTime();
  }


  private static Object transformNode(Node node) {
    List<String> tagNames = node.tagNames();
    if (tagNames.size() != 1) {
      log.warn("Sorry there is no parse implements for multi tags node: {}", node);
      return node;
    }

    String tagName = tagNames.get(0);

    MapperContext mapperContext = MapperProxy.ENV.getMapperContext();
    Map<String, Class<?>> tagTypeMapping = mapperContext.getTagTypeMapping();

    Class<?> nodeType = tagTypeMapping.get(tagName);
    if (nodeType != null) {
      return nodeToResultType(node, nodeType);
    }
    return node;
  }

  private static Object transformMap(HashMap<String, ValueWrapper> map) {
    HashMap<Object, Object> javaResult = new HashMap<>();
    for (Map.Entry<String, ValueWrapper> entry : map.entrySet()) {
      String k = entry.getKey();
      ValueWrapper v = entry.getValue();
      javaResult.put(k, getValue(v));
    }
    return javaResult;
  }

  private static Object transformList(ArrayList<ValueWrapper> list) {
    return list.stream().map(ResultSetUtil::getValue).collect(Collectors.toList());
  }

  /**
   * <p>数据库中的节点类型转接口类型（节点orm）</p>
   * @param v 结果集中的 node 数据值
   * @param resultType 接口返回值类型（类型为集合时，为集合泛型）
   * @param <T> 调用方用来接收结果的类型，即 resultType
   * @return resultType类型对应的实例
   */
  public static <T> T nodeToResultType(Node v, Class<T> resultType) {
    T t = null;
    try {
      List<ValueWrapper> values = v.values(v.tagNames().get(0));
      List<String> keys = v.keys(v.tagNames().get(0));
      t = resultType.newInstance();
      for (int i = 0; i < keys.size(); i++) {
        String prop = keys.get(i);
        ReflectUtil.setValue(t, prop, ResultSetUtil.getValue(values.get(i)));
      }
      setId(t, resultType, v);
    } catch (UnsupportedEncodingException | InstantiationException
      | IllegalAccessException | NoSuchFieldException e) {
      throw new ResultHandleException(
        String.format("%s : %s", e.getClass().toString(), e.getMessage()));
    }
    return t;
  }

  /**
   * <p>数据库中的节点类型转接口类型，并填充到参数 o 中</p>
   * @param o resultType类型的数据容器
   * @param fieldName o 中，与 node 对应的属性名
   * @param node nebula中的节点类型
   */
  public static void nodeToResultType(Object o, String fieldName, Node node) {
    Class<?> fieldType = ReflectUtil.fieldType(o, fieldName);
    if (fieldType != null) {
      Object fieldValue = nodeToResultType(node, fieldType);
      try {
        ReflectUtil.setValue(o, fieldName, fieldValue);
      } catch (NoSuchFieldException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * <p>数据库中的关系类型转接口类型（关系orm）</p>
   * @param r 结果集中的 relationship 数据值
   * @param resultType 接口返回值类型（类型为集合时，为集合泛型）
   * @param <T> 调用方用来接收结果的类型，即 resultType
   * @return resultType类型对应的实例
   */
  public static <T> T relationshipToResultType(Relationship r, Class<T> resultType) {
    T t = null;
    try {
      t = resultType.newInstance();
      HashMap<String, ValueWrapper> properties = r.properties();
      for (Map.Entry<String, ValueWrapper> entry : properties.entrySet()) {
        ReflectUtil.setValue(t, entry.getKey(), ResultSetUtil.getValue(entry.getValue()));
      }
    } catch (UnsupportedEncodingException | InstantiationException
      | NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return t;
  }

  /**
   * <p>数据库中的关系类型转接口类型（关系orm）</p>
   * @param o 结果容器
   * @param fieldName o 中，与relationship对应的属性名
   * @param relationship 结果集中的 relationship 数据值
   */
  public static void relationshipToResultType(Object o, String fieldName,
      Relationship relationship) {
    Class<?> fieldType = ReflectUtil.fieldType(o, fieldName);
    if (fieldType != null) {
      Object fieldValue = relationshipToResultType(relationship, fieldType);
      try {
        ReflectUtil.setValue(o, fieldName, fieldValue);
      } catch (NoSuchFieldException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * <p>从 resultType 中获取到用 @Id 注解的属性，
   * 并将nebula中用 id(n) 函数获得的值填入。</p>
   * @param obj java对象
   * @param resultType xml中声明的 resultType，
   *                   当returnType是集合时，为范型。否则与 returnType 相同
   * @param v 节点类型
   * @throws IllegalAccessException 当 id 值的类型，与
   *     v 中，通过 id(n) 获取到的类型不匹配时报错
   */
  public static void setId(Object obj, Class<?> resultType, Node v)
      throws IllegalAccessException {
    Field pkField = getPkField(resultType);
    ValueWrapper idWrapper = v.getId();
    Object id = ResultSetUtil.getValue(idWrapper);
    ReflectUtil.setValue(obj, pkField, id);
  }

  /**
   * 是否命中指定错误枚举
   * @param resultSet 执行结果
   * @param errorCodes 错误枚举
   * @return 是否命中指定错误枚举
   */
  public static boolean isMatchedErrorCode(ResultSet resultSet, ErrorCode... errorCodes) {
    if (resultSet == null || errorCodes == null) {
      return false;
    }
    for (ErrorCode code : errorCodes) {
      if (resultSet.getErrorCode() == code.getValue()){
        return true;
      }
    }
    return false;
  }

  /**
   * 是否命中session失效情况
   * @param resultSet 执行结果
   * @return 是否命中session失效情况
   */
  public static boolean isSessionError(ResultSet resultSet) {
    return isMatchedErrorCode(resultSet, ErrorCode.E_SESSION_INVALID,
            ErrorCode.E_SESSION_NOT_FOUND, ErrorCode.E_SESSION_TIMEOUT);
  }

  /**
   * nebula重启后, 对应的session执行的第一条gql需要选择space
   * @param resultSet 执行结果
   * @return 是否命中语法错误情况
   */
  public static boolean isSemanticError(ResultSet resultSet) {
    return isMatchedErrorCode(resultSet, ErrorCode.E_SEMANTIC_ERROR);
  }

}

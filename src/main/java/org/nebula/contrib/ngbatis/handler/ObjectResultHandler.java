package org.nebula.contrib.ngbatis.handler;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getCollectionE;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.isCurrentTypeOrParentType;
import static org.nebula.contrib.ngbatis.utils.ResultSetUtil.nodeToResultType;
import static org.nebula.contrib.ngbatis.utils.ResultSetUtil.relationshipToResultType;

import com.vesoft.nebula.client.graph.data.Node;
import com.vesoft.nebula.client.graph.data.Relationship;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.nebula.contrib.ngbatis.config.NgbatisConfig;
import org.nebula.contrib.ngbatis.models.MapperContext;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.nebula.contrib.ngbatis.utils.ReflectUtil;
import org.nebula.contrib.ngbatis.utils.ResultSetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 结果集数据类型转换器。
 * <p> ResultSet -&gt;  Object </p>
 *
 * @author yeweicheng
 * @since 2022-06-10 22:53
 * <br>Now is history!
 */
@Component
public class ObjectResultHandler extends AbstractResultHandler<Object, Object> {

  private final Map<Class<?>, Field[]> classDeclaredFieldsMap = new HashMap<>();
  private final Map<Class<?>, Field[]> classAllDeclaredFieldsMap = new HashMap<>();
  private final Map<Class<?>, Set<?>> classColumnNamesMap = new HashMap<>();
  private final Map<Class<?>, Set<?>> classAllColumnNamesMap = new HashMap<>();
  
  @Autowired private NgEdgeResultHandler edgeResultHandler;
  @Autowired private NgVertexResultHandler vertexResultHandler;
  @Lazy @Autowired private CollectionObjectResultHandler collectionObjectResultHandler;
  
  @Override
  public Object handle(Object newResult, ResultSet result, Class resultType)
      throws NoSuchFieldException, IllegalAccessException {
    if (result.rowsSize() == 0) {
      return null;
    }
    List<String> columnNames = result.getColumnNames();
    ResultSet.Record record = result.rowValues(0);
    return handle(newResult, record, columnNames, resultType);
  }

  /**
   * 对象结果orm方法
   * @param newResult 与dao返回值类型对应类型的对象
   * @param record 结果集中的记录
   * @param columnNames 结果集涉及的列名
   * @param resultType 接口返回值类型（类型为集合时，为集合泛型）
   * @return dao接口返回值
   * @throws NoSuchFieldException 通过列名在对象中找不到对应属性
   * @throws IllegalAccessException 列名对应类型与实体对象属性类型不匹配时的异常
   */
  public Object handle(
      Object newResult,
      ResultSet.Record record,
      List<String> columnNames,
      Class<?> resultType) throws NoSuchFieldException, IllegalAccessException {

    for (int i = 0; i < columnNames.size(); i++) {
      ValueWrapper valueWrapper = record.values().get(i);
      Object v = ResultSetUtil.getValue(valueWrapper, resultType);
      newResult = fillResult(v, newResult, columnNames, resultType, i);
    }

    return newResult;
  }

  private Object fillResult(Object v, Object newResult, List<String> columnNames,
      Class resultType, int i)
      throws NoSuchFieldException, IllegalAccessException {
    String columnName = columnNames.get(i);
    if (valIsResultType(v, resultType)) {
      newResult = v;
    } else if (v instanceof Node) {
      newResult = fillResultByNode(
        (Node) v,
        newResult,
        columnNames,
        resultType,
        columnName
      );
    } else if (v instanceof Relationship) {
      newResult = fillResultByRelationship(
        (Relationship) v,
        newResult,
        columnNames,
        resultType,
        columnName
      );
    } else if (valIsProps(v, resultType)) {
      Map<?, ?> props = (Map<?, ?>) v;
      Set<?> allColumn = computeAllColumn(resultType);
      Set<?> retains = new HashSet<>(props.keySet());
      retains.retainAll(allColumn);
      for (Object o : retains) {
        String k = String.valueOf(o);
        Object prop = props.get(k);
        ReflectUtil.setValue(newResult, k, prop);
      }
    } else if (v instanceof Collection) {
      Class<?> genericType = getCollectionE(newResult, columnName);
      Collection<?> innerCollectionResult = 
        collectionObjectResultHandler.handle((Collection) v, genericType);
      ReflectUtil.setValue(newResult, columnName, innerCollectionResult);
    } else {
      ReflectUtil.setValue(newResult, columnName, v);
    }
    return newResult;
  }
  
  private boolean valIsProps(Object v, Class<?> resultType) {
    NgbatisConfig ngbatisConfig = MapperContext.newInstance().getNgbatisConfig();
    if (!ngbatisConfig.getEnablePropMapping()) {
      return false;
    }
    // 从属性获取的一个前提是，栏位值本身是一个map
    if (v instanceof Map) {
      Map<?, ?> m = (Map<?, ?>) v;
      Set<?> columnNames = computeColumnNames(resultType);
      return m.keySet().containsAll(columnNames);
    }
    return false;
  }
  
  private Set<?> computeAllColumn(Class<?> resultType) {
    Field[] fs = classAllDeclaredFieldsMap.computeIfAbsent(
      resultType,
      (k) -> ReflectUtil.getAllColumnFields(k, true)
    );

    return classAllColumnNamesMap.computeIfAbsent(
      resultType,
      (k) -> Arrays.stream(fs)
        .map(ReflectUtil::getNameByColumn)
        .collect(Collectors.toSet())
    );
  }
  
  private Set<?> computeColumnNames(Class<?> resultType) {
    Field[] fs = classDeclaredFieldsMap.computeIfAbsent(
      resultType,
      (k) -> ReflectUtil.getColumnFields(k, true)
    );
    return classColumnNamesMap.computeIfAbsent(
      resultType,
      (k) -> Arrays.stream(fs)
        .map(ReflectUtil::getNameByColumn)
        .collect(Collectors.toSet())
    );
  }

  private boolean valIsResultType(Object v, Class resultType) {
    return v != null && isCurrentTypeOrParentType(v.getClass(), resultType);
  }

  private Object fillResultByNode(
      Node node, Object newResult,
      List<String> columnNames, Class resultType, String columnName) {

    if (columnNames.size() == 1) {
      newResult = nodeToResultType(node, resultType);
    } else {
      nodeToNgResultType(node, newResult, columnName);
    }
    return newResult;
  }

  private void nodeToNgResultType(Node node, Object newResult, String columnName) {
    Class<?> fieldType = ReflectUtil.fieldType(newResult, columnName);
    if (fieldType == NgVertex.class) {
      try {
        NgVertex<?> ngVertex = toVertex(node);
        ReflectUtil.setValue(newResult, columnName, ngVertex);
      } catch (Exception e) {
        // 在前置判断中，已经规避了异常发生的可能性
        throw new RuntimeException(e);
      }
    } else {
      nodeToResultType(newResult, columnName, node);
    }
  }

  private Object fillResultByRelationship(
      Relationship relationship, Object newResult,
      List<String> columnNames, Class resultType, String columnName) {

    if (columnNames.size() == 1) {
      newResult = relationshipToResultType(relationship, resultType);
    } else {
      relationshipToNgResultType(relationship, newResult, columnName);
    }
    return newResult;
  }

  private void relationshipToNgResultType(
      Relationship relationship, Object newResult, String columnName) {

    Class<?> fieldType = ReflectUtil.fieldType(newResult, columnName);
    if (fieldType == NgEdge.class) {
      try {
        NgEdge<?> ngEdge = toEdge(relationship);
        ReflectUtil.setValue(newResult, columnName, ngEdge);
      } catch (Exception e) {
        // 在前置判断中，已经规避了异常发生的可能性
        throw new RuntimeException(e);
      }
    } else {
      relationshipToResultType(newResult, columnName, relationship);
    }
  }

  public NgVertex<?> toVertex(Node node) {
    NgVertex<?> ngVertex = new NgVertex<>();
    vertexResultHandler.handle(ngVertex, node);
    return ngVertex;
  }

  public NgEdge<?> toEdge(Relationship relationship) throws UnsupportedEncodingException {
    NgEdge<?> ngEdge = new NgEdge<>();
    edgeResultHandler.handle(ngEdge, relationship);
    return ngEdge;
  }
}

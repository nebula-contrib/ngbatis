package org.nebula.contrib.ngbatis.handler;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.utils.ReflectUtil.isCurrentTypeOrParentType;
import static org.nebula.contrib.ngbatis.utils.ResultSetUtil.nodeToResultType;
import static org.nebula.contrib.ngbatis.utils.ResultSetUtil.relationshipToResultType;

import com.vesoft.nebula.client.graph.data.Node;
import com.vesoft.nebula.client.graph.data.Relationship;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.util.List;
import org.nebula.contrib.ngbatis.utils.ReflectUtil;
import org.nebula.contrib.ngbatis.utils.ResultSetUtil;
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
      Object v = ResultSetUtil.getValue(valueWrapper);
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
    } else {
      ReflectUtil.setValue(newResult, columnName, v);
    }
    return newResult;
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
      nodeToResultType(newResult, columnName, node);
    }
    return newResult;
  }

  private Object fillResultByRelationship(
      Relationship relationship, Object newResult,
      List<String> columnNames, Class resultType, String columnName) {

    if (columnNames.size() == 1) {
      newResult = relationshipToResultType(relationship, resultType);
    } else {
      relationshipToResultType(newResult, columnName, relationship);
    }
    return newResult;
  }
}

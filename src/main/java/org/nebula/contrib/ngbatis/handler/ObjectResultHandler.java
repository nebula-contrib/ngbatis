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

/**.
 * 结果集数据类型转换器.
 *.
 * <p>ResultSet -&gt; Object.
 *.
 * @author yeweicheng.
 * @since 2022-06-10 22:53 <br>.
 *     Now is history.
.*/
@Component
public class ObjectResultHandler extends AbstractResultHandler
    <Object, Object> {

  @Override
  public Object handle(
    final Object newResult, final ResultSet result, final Class resultType)
      throws NoSuchFieldException, IllegalAccessException {
    if (result.rowsSize() == 0) {
      return null;
    }
    List<String> columnNames = result.getColumnNames();
    ResultSet.Record record = result.rowValues(0);
    return handle(newResult, record, columnNames, resultType);
  }

  public Object handle(
        Object newResult, final ResultSet.Record record,
        final List<String> columnNames, final Class<?> resultType)
      throws NoSuchFieldException, IllegalAccessException {

    for (int i = 0; i < columnNames.size(); i++) {
      ValueWrapper valueWrapper = record.values().get(i);
      Object v = ResultSetUtil.getValue(valueWrapper);
      newResult = fillResult(v, newResult, columnNames, resultType, i);
    }

    return newResult;
  }

  private Object fillResult(
        final Object v, Object newResult, final List<String> columnNames,
        final Class resultType, final int i)
      throws NoSuchFieldException, IllegalAccessException {
    String columnName = columnNames.get(i);
    if (vIsResultType(v, resultType)) {
      newResult = v;
    } else if (v instanceof Node) {
      newResult = fillResultByNode(
        (Node) v, newResult, columnNames, resultType, columnName);
    } else if (v instanceof Relationship) {
      newResult =
          fillResultByRelationship(
              (Relationship) v, newResult, columnNames, resultType,
              columnName);
    } else {
      ReflectUtil.setValue(newResult, columnName, v);
    }
    return newResult;
  }

  private boolean vIsResultType(final Object v, final Class resultType) {
    return v != null && isCurrentTypeOrParentType(v.getClass(), resultType);
  }

  private Object fillResultByNode(
    final Node node, Object newResult,
    final List<String> columnNames, final Class resultType,
    final String columnName) {
      if (columnNames.size() == 1) {
          newResult = nodeToResultType(node, resultType);
      } else {
        nodeToResultType(newResult, columnName, node);
      }
      return newResult;
    }

  private Object fillResultByRelationship(
      final Relationship relationship,
      Object newResult,
      final List<String> columnNames,
      final Class resultType,
      final String columnName) {

    if (columnNames.size() == 1) {
      newResult = relationshipToResultType(relationship, resultType);
    } else {
      relationshipToResultType(newResult, columnName, relationship);
    }
    return newResult;
  }
}

package org.nebula.contrib.ngbatis.handler;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.utils.ResultSetUtil.nodePropsToMap;

import com.vesoft.nebula.client.graph.data.Node;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ResultSet.Record;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.nebula.contrib.ngbatis.exception.ResultHandleException;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.nebula.contrib.ngbatis.utils.ResultSetUtil;
import org.springframework.stereotype.Component;

/**
 * Convert the vertex data from ResultSet to NgVertex. 
 * @author yeweicheng
 * @since 2023-01-07 4:55
 *   <br> Now is history!
 */
@Component
public class NgVertexResultHandler extends AbstractResultHandler<NgVertex<?>, NgVertex<?>> {

  @Override
  public NgVertex<?> handle(NgVertex<?> newResult, ResultSet result, Class resultType)
      throws NoSuchFieldException, IllegalAccessException, InstantiationException {
    Record row = result.rowValues(0);
    return handle(newResult, row);
  }

  public NgVertex<?> handle(NgVertex<?> newResult, Record row) {
    ValueWrapper node = row.get(0);
    handle(newResult, node);
    return newResult;
  }
  
  public NgVertex<?> handle(NgVertex<?> newResult, ValueWrapper nodeValueWrapper) {
    try {
      Node node = nodeValueWrapper.asNode();
      ValueWrapper id = node.getId();
      newResult.setVid(ResultSetUtil.getValue(id));
      List<String> tags = node.tagNames();
      newResult.setTags(tags);
      newResult.setProperties(nodePropsToMap(node));
      return newResult;
    } catch (UnsupportedEncodingException e) {
      throw new ResultHandleException(
          String.format("%s : %s", e.getClass().toString(), e.getMessage()));
    }
  }
}

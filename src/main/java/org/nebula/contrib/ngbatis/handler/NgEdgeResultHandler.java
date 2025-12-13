package org.nebula.contrib.ngbatis.handler;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.utils.ResultSetUtil.edgePropsToMap;
import static org.nebula.contrib.ngbatis.utils.ResultSetUtil.getValue;

import com.vesoft.nebula.client.graph.data.Relationship;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ResultSet.Record;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.io.UnsupportedEncodingException;
import org.nebula.contrib.ngbatis.exception.ResultHandleException;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.springframework.stereotype.Component;

/**
 * Convert the edge data from ResultSet to NgVertex. 
 * @author yeweicheng
 * @since 2023-01-07 4:54
 *   <br> Now is history!
 */
@Component
public class NgEdgeResultHandler extends AbstractResultHandler<NgEdge<?>, NgEdge<?>> {

  @Override
  public NgEdge<?> handle(NgEdge<?> newResult, ResultSet result, Class resultType)
      throws NoSuchFieldException, IllegalAccessException, InstantiationException {
    Record row = result.rowValues(0);
    return handle(newResult, row);
  }
  
  public NgEdge<?> handle(NgEdge<?> newResult, Record row) {
    ValueWrapper relationship = row.get(0);
    return handle(newResult, relationship);
  }
  
  public NgEdge<?> handle(NgEdge<?> newResult, ValueWrapper relationshipValueWrapper) {
    try {
      Relationship relationship = relationshipValueWrapper.asRelationship();
      return handle(newResult, relationship);
    } catch (UnsupportedEncodingException e) {
      throw new ResultHandleException(
          String.format("%s : %s", e.getClass().toString(), e.getMessage()));
    }
  }

  public NgEdge<?> handle(NgEdge<?> newResult, Relationship relationship)
    throws UnsupportedEncodingException {
    newResult.setEdgeName(relationship.edgeName());

    long ranking = relationship.ranking();
    newResult.setRank(ranking);

    newResult.setSrcID(getValue(relationship.srcId()));
    newResult.setDstID(getValue(relationship.dstId()));

    newResult.setProperties(edgePropsToMap(relationship));
    return newResult;
  }

}

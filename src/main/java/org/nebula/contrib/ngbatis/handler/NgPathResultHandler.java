package org.nebula.contrib.ngbatis.handler;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.data.PathWrapper;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ResultSet.Record;
import org.nebula.contrib.ngbatis.models.data.NgPath;
import org.nebula.contrib.ngbatis.utils.ResultSetUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

/**
 * Convert the path data from ResultSet to NgPath. 
 * @author yeweicheng
 * @since 2023-01-07 4:54
 *   <br> Now is history!
 */
@Component
public class NgPathResultHandler extends AbstractResultHandler<NgPath<?>, NgPath<?>> {

  @Resource
  private NgVertexResultHandler ngVertexResultHandler;

  @Override
  public NgPath<?> handle(NgPath<?> newResult, ResultSet result, Class resultType)
      throws NoSuchFieldException, IllegalAccessException, InstantiationException {
    Record record = result.rowValues(0);
    return handle(newResult, record);
  }

  public NgPath<?> handle(NgPath<?> newResult, Record record) {
    PathWrapper pathWrapper = ResultSetUtil.getValue(record.values().get(0));
    pathWrapper.getSegments().forEach(segment -> {
      try {
        NgPath.Relationship ngRelationship = new NgPath.Relationship();
        long ranking = segment.getRelationShip().ranking();
        Object srcId = ResultSetUtil.getValue(segment.getStartNode().getId());
        Object dstId = ResultSetUtil.getValue(segment.getEndNode().getId());
        String edgeName = segment.getRelationShip().edgeName();
        ngRelationship.setRank(ranking);
        ngRelationship.setSrcID(srcId);
        ngRelationship.setDstID(dstId);
        ngRelationship.setEdgeName(edgeName);
        ngRelationship.setProperties(ResultSetUtil.edgePropsToMap(segment.getRelationShip()));

        ngVertexResultHandler.handle(ngRelationship.getDst(), segment.getEndNode());
        ngVertexResultHandler.handle(ngRelationship.getSrc(), segment.getStartNode());

        newResult.getRelationships().add(ngRelationship);
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    });
    return newResult;
  }
}

package org.nebula.contrib.kg.ngbatis;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ResultSet.Record;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import org.nebula.contrib.kg.pojo.Triplet;
import org.nebula.contrib.ngbatis.handler.AbstractResultHandler;
import org.nebula.contrib.ngbatis.handler.NgEdgeResultHandler;
import org.nebula.contrib.ngbatis.handler.NgVertexResultHandler;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yeweicheng
 * @since 2024-08-28 13:50
 * <br>Now is history!
 */
@Component
public class TripletResultHandler extends AbstractResultHandler<Triplet<String>, Triplet<String>> {

  @Autowired
  private NgVertexResultHandler vertexHandler;
  @Autowired
  private NgEdgeResultHandler edgeHandler;

  @Override
  public Triplet<String> handle(
      Triplet<String> newResult, ResultSet result, Class resultType) {
    return handle(newResult, result.rowValues(0));
  }

  public Triplet<String> handle(Triplet<String> newResult, Record row) {

    NgVertex<String> src = new NgVertex<>();

    ValueWrapper valueWrapper = row.get(0);
    vertexHandler.handle(src, valueWrapper);
    newResult.setSrc(src);

    NgEdge<String> edge = new NgEdge<>();
    valueWrapper = row.get(1);
    edgeHandler.handle(edge, valueWrapper);
    newResult.setEdge(edge);

    NgVertex<String> dst = new NgVertex<>();
    valueWrapper = row.get(2);
    vertexHandler.handle(dst, valueWrapper);
    newResult.setDst(dst);

    return newResult;
  }
}

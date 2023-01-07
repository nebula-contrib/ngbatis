package org.nebula.contrib.ngbatis.handler;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ResultSet.Record;
import java.util.Collection;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Convert the vertex data from ResultSet to collection of NgVertex.
 * @author yeweicheng
 * @since 2023-01-07 4:57
 *   <br> Now is history!
 */
@Component
public class CollectionNgVertexResultHandler 
    extends AbstractResultHandler<Collection, NgVertex<?>> {

  @Autowired private NgVertexResultHandler singleHandler;

  @Override
  public Collection handle(Collection newResult, ResultSet result, Class resultType)
      throws NoSuchFieldException, IllegalAccessException, InstantiationException {
    int rowSize = result.rowsSize();
    for (int i = 0; i < rowSize; i++) {
      Record row = result.rowValues(i);
      NgVertex<?> vertex = new NgVertex<>();
      vertex = singleHandler.handle(vertex, row);
      newResult.add(vertex);
    }
    return newResult;
  }
}

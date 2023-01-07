package org.nebula.contrib.ngbatis.handler;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ResultSet.Record;
import java.util.Collection;
import org.nebula.contrib.ngbatis.models.data.NgSubgraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Convert the subgraph data from ResultSet to collection of NgSubgraph.
 * @author yeweicheng
 * @since 2023-01-07 4:57
 *   <br> Now is history!
 */
@Component
public class CollectionNgSubgraphResultHandler 
    extends AbstractResultHandler<Collection, NgSubgraph<?>> {

  @Autowired private NgSubgraphResultHandler singleHandler;

  @Override
  public Collection handle(Collection newResult, ResultSet result, Class resultType)
      throws NoSuchFieldException, IllegalAccessException, InstantiationException {
    int rowSize = result.rowsSize();
    for (int i = 0; i < rowSize; i++) {
      Record row = result.rowValues(i);
      NgSubgraph<?> subgraph = new NgSubgraph<>();
      subgraph = singleHandler.handle(subgraph, row);
      newResult.add(subgraph);
    }
    return newResult;
  }
}

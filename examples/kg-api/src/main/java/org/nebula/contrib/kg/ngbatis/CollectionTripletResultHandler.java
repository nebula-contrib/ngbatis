package org.nebula.contrib.kg.ngbatis;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.data.ResultSet;
import java.util.Collection;
import org.nebula.contrib.kg.pojo.Triplet;
import org.nebula.contrib.ngbatis.handler.AbstractResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yeweicheng
 * @since 2024-08-28 13:59
 * <br>Now is history!
 */
@Component
public class CollectionTripletResultHandler extends
  AbstractResultHandler<Collection, Triplet<String>> {

  @Autowired
  private TripletResultHandler singleHandler;

  @Override
  public Collection handle(Collection newResult, ResultSet result, Class resultType)
      throws NoSuchFieldException, IllegalAccessException, InstantiationException {

    int rowsSize = result.rowsSize();
    for (int i = 0; i < rowsSize; i++) {
      ResultSet.Record row = result.rowValues(i);
      Triplet<String> triplet = new Triplet<>();
      triplet = singleHandler.handle(triplet, row);
      newResult.add(triplet);
    }
    return newResult;
  }

}

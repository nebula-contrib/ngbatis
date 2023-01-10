package org.nebula.contrib.ngbatis.handler;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ResultSet.Record;
import java.util.Collection;
import org.nebula.contrib.ngbatis.models.data.NgPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Convert the path data from ResultSet to collection of NgPath.
 * @author yeweicheng
 * @since 2023-01-07 4:57
 *   <br> Now is history!
 */
@Component
public class CollectionNgPathResultHandler extends AbstractResultHandler<Collection, NgPath<?>> {

  @Autowired private NgPathResultHandler singleHandler;

  @Override
  public Collection handle(Collection newResult, ResultSet result, Class resultType)
      throws NoSuchFieldException, IllegalAccessException, InstantiationException {
    int rowsSize = result.rowsSize();
    for (int i = 0; i < rowsSize; i++) {
      Record row = result.rowValues(i);
      NgPath<?> ngPath = new NgPath<>();
      ngPath = singleHandler.handle(ngPath, row);
      newResult.add(ngPath);
    }
    return newResult;
  }
}

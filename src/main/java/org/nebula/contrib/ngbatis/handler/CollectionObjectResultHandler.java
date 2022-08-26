package org.nebula.contrib.ngbatis.handler;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import com.vesoft.nebula.client.graph.data.ResultSet;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 结果集数据类型转换器
 *
 * <p>ResultSet -&gt; Collection&lt;Object&gt;
 *
 * @author yeweicheng
 * @since 2022-06-10 22:31 <br>
 *     Now is history!
 */
@Component
public class CollectionObjectResultHandler extends AbstractResultHandler<Collection, Object> {

  @Autowired private ObjectResultHandler objectResultHandler;

  @Override
  public Collection handle(Collection newResult, ResultSet result, Class resultType)
      throws NoSuchFieldException, IllegalAccessException, InstantiationException {
    List<String> columnNames = result.getColumnNames();
    int size = result.rowsSize();
    for (int i = 0; i < size; i++) {
      Object o = resultType.newInstance();
      ResultSet.Record record = result.rowValues(i);
      o = objectResultHandler.handle(o, record, columnNames, resultType);
      newResult.add(o);
    }
    return newResult;
  }
}

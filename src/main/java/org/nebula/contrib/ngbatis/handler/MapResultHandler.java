package org.nebula.contrib.ngbatis.handler;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.util.List;
import java.util.Map;
import org.nebula.contrib.ngbatis.utils.ResultSetUtil;
import org.springframework.stereotype.Component;

/**
 * 结果集数据类型转换器
 * <p> ResultSet -&gt;  Collection&lt;String&gt; </p>
 *
 * @author yeweicheng
 * @since 2022-06-10 17:15
 * <br>Now is history!
 */
@Component
public class MapResultHandler extends AbstractResultHandler<Map, Map> {

  @Override
  public Map handle(Map newResult, ResultSet result, Class resultType) {
    List<String> columnNames = result.getColumnNames();
    ResultSet.Record record = result.rowValues(0);
    for (int i = 0; i < columnNames.size(); i++) {
      ValueWrapper valueWrapper = record.values().get(i);
      newResult.put(columnNames.get(i), ResultSetUtil.getValue(valueWrapper));
    }
    return newResult;
  }
}

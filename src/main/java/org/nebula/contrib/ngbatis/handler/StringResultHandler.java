package org.nebula.contrib.ngbatis.handler;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import org.nebula.contrib.ngbatis.utils.ResultSetUtil;
import org.springframework.stereotype.Component;

/**
 * 结果集数据类型转换器
 * <p> ResultSet -&gt;  String</p>
 *
 * @author yeweicheng
 * @since 2022-06-11 2:29
 * <br>Now is history!
 */
@Component
public class StringResultHandler extends AbstractResultHandler<String, String> {

  @Override
  public String handle(String newResult, ResultSet result, Class resultType)
      throws NoSuchFieldException, IllegalAccessException, InstantiationException {
    ResultSet.Record record = result.rowValues(0);
    ValueWrapper valueWrapper = record.values().get(0);
    Object value = ResultSetUtil.getValue(valueWrapper);
    return value == null ? null : value.toString();
  }

}

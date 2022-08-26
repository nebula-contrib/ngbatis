package org.nebula.contrib.ngbatis.handler;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import org.nebula.contrib.ngbatis.utils.ResultSetUtil;
import org.springframework.stereotype.Component;

/**.
 * 结果集数据类型转换器.
 *.
 * <p>ResultSet -&gt; Boolean.
 *.
 * @author yeweicheng.
 * @since 2022-06-11 3:04 <br>.
 *     Now is history.
.*/
@Component
public class BooleanResultHandler extends AbstractResultHandler
    <Boolean, Boolean> {

  @Override
  public Boolean handle(
      final Boolean newResult, final ResultSet result,
      final finalClass resultType)
    throws NoSuchFieldException, IllegalAccessException,
    InstantiationException {
      ResultSet.Record record = result.rowValues(0);
      ValueWrapper valueWrapper = record.values().get(0);
      return ResultSetUtil.getValue(valueWrapper);
  }
}

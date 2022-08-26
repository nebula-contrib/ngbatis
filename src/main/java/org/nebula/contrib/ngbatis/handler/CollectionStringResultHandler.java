package org.nebula.contrib.ngbatis.handler;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.util.Collection;
import java.util.List;
import org.nebula.contrib.ngbatis.utils.ResultSetUtil;
import org.springframework.stereotype.Component;

/**.
 * 结果集数据类型转换器.
 *.
 * <p>ResultSet -&gt; Collection&lt;String&gt;.
 *.
 * @author yeweicheng.
 * @since 2022-06-10 17:11 <br>.
 *     Now is history.
.*/
@Component
public class CollectionStringResultHandler extends AbstractResultHandler
    <Collection, String> {

  @Override
  public Collection handle(
      final Collection newResult, final ResultSet result,
      final Class resultType) {
    List<String> columnNames = result.getColumnNames();
    String firstCol = columnNames.get(0);
    List<ValueWrapper> valueWrappers = result.colValues(firstCol);
    valueWrappers.forEach(i -> newResult.add(ResultSetUtil.getValue(i)));
    return newResult;
  }
}

package org.nebula.contrib.ngbatis.handler;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.castNumber;

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.util.Collection;
import org.nebula.contrib.ngbatis.utils.ResultSetUtil;
import org.springframework.stereotype.Component;

/**
 * 结果集数据类型转换器。
 * <p> ResultSet -&gt; Collection&lt;Number&gt; </p>
 * @author xYLiuuuuuu
 * @since 2024/10/1 16:29
 */
@Component
public class CollectionNumberResultHandler extends AbstractResultHandler<Collection, Number> {

  @Override
  public Collection handle(Collection newResult, ResultSet result, Class resultType)
      throws NoSuchFieldException, IllegalAccessException, InstantiationException {
    int size = result.rowsSize();
    for (int i = 0; i < size; i++) {
      ResultSet.Record record = result.rowValues(i);
      ValueWrapper valueWrapper = record.values().get(0);
      Number value = ResultSetUtil.getValue(valueWrapper);
      newResult.add(castNumber(value, resultType));
    }
    return newResult;
  }
}

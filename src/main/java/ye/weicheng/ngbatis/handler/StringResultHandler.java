// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.handler;

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import org.springframework.stereotype.Component;
import ye.weicheng.ngbatis.utils.ResultSetUtil;

/**
 * 结果集数据类型转换器
 * <p> ResultSet -> String</p>
 *
 * @author yeweicheng
 * @since 2022-06-11 2:29
 * <br>Now is history!
 */
@Component
public class StringResultHandler extends AbstractResultHandler<String, String> {

    @Override
    public String handle(String newResult, ResultSet result, Class resultType) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        ResultSet.Record record = result.rowValues(0);
        ValueWrapper valueWrapper = record.values().get(0);
        Object value = ResultSetUtil.getValue(valueWrapper);
        return value == null ? null : value.toString();
    }

}

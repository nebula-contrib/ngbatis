// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.handler;

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import org.springframework.stereotype.Component;
import ye.weicheng.ngbatis.utils.ResultSetUtil;

import java.util.List;
import java.util.Map;

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
            newResult.put( columnNames.get(i ), ResultSetUtil.getValue( valueWrapper ));
        }
        return newResult;
    }
}

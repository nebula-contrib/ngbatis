// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.handler;

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import org.springframework.stereotype.Component;
import ye.weicheng.ngbatis.utils.ResultSetUtil;

import java.util.*;

/**
 * 结果集数据类型转换器
 * <p> ResultSet -&gt; Collection&lt;Map&gt; </p>
 *
 * @author yeweicheng
 * @since 2022-06-11 3:04
 * <br>Now is history!
 */
@Component
public class CollectionMapResultHandler extends AbstractResultHandler<Collection, Map>{

    @Override
    public Collection handle(Collection newResult, ResultSet result, Class resultType) {
        List<String> columnNames = result.getColumnNames();
        int size = result.rowsSize();
        for (int i = 0; i < size; i++) {
            Map row = new HashMap();
            ResultSet.Record record = result.rowValues(i);
            for (int j = 0; j < columnNames.size(); j++) {
                ValueWrapper valueWrapper = record.values().get(j);
                row.put( columnNames.get( j ), ResultSetUtil.getValue( valueWrapper ));
            }
            newResult.add( row );
        }
        return newResult;
    }

}
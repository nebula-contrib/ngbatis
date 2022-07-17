// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.handler;

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import org.springframework.stereotype.Component;
import ye.weicheng.ngbatis.utils.ResultSetUtil;

import java.util.Collection;
import java.util.List;

/**
 * 结果集数据类型转换器
 * <p> ResultSet -&gt; Collection&lt;String&gt; </p>
 *
 * @author yeweicheng
 * @since 2022-06-10 17:11
 * <br>Now is history!
 */
@Component
public class CollectionStringResultHandler extends AbstractResultHandler<Collection, String> {

    @Override
    public Collection handle(Collection newResult, ResultSet result, Class resultType) {
        List<String> columnNames = result.getColumnNames();
        String firstCol  = columnNames.get(0);
        List<ValueWrapper> valueWrappers = result.colValues(firstCol);
        valueWrappers.forEach(i -> newResult.add( ResultSetUtil.getValue( i )));
        return newResult;
    }
}

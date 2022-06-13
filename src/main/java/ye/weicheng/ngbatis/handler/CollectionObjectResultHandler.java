// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.handler;

import com.vesoft.nebula.client.graph.data.Node;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import org.springframework.stereotype.Component;
import ye.weicheng.ngbatis.utils.ReflectUtil;
import ye.weicheng.ngbatis.utils.ResultSetUtil;

import java.util.*;

import static ye.weicheng.ngbatis.utils.ResultSetUtil.nodeToResultType;

/**
 * 结果集数据类型转换器
 * <p> ResultSet -> Collection&lt;Object&gt; </p>
 *
 * @author yeweicheng
 * @since 2022-06-10 22:31
 * <br>Now is history!
 */
@Component
public class CollectionObjectResultHandler extends AbstractResultHandler<Collection, Object> {

    @Override
    public Collection handle(Collection newResult, ResultSet result, Class resultType ) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        List<String> columnNames = result.getColumnNames();
        int size = result.rowsSize();
        for (int i = 0; i < size; i++) {
            Object o = resultType.newInstance();
            ResultSet.Record record = result.rowValues(i);
            for (int j = 0; j < columnNames.size(); j++) {
                ValueWrapper valueWrapper = record.values().get(j);
                Object v = ResultSetUtil.getValue( valueWrapper );
                if ( columnNames.size() == 1 && v instanceof Node) {
                    o = nodeToResultType( (Node)v, resultType );
                } else {
                    ReflectUtil.setValue( o, columnNames.get(j), ResultSetUtil.getValue( valueWrapper ));
                }
            }
            newResult.add( o );
        }
        return newResult;
    }
}

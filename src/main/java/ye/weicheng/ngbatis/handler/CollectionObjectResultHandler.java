// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.handler;

import com.vesoft.nebula.client.graph.data.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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

    @Autowired
    private ObjectResultHandler objectResultHandler;

    @Override
    public Collection handle(Collection newResult, ResultSet result, Class resultType ) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        List<String> columnNames = result.getColumnNames();
        int size = result.rowsSize();
        for (int i = 0; i < size; i++) {
            Object o = resultType.newInstance();
            ResultSet.Record record = result.rowValues(i);
            o = objectResultHandler.handle( o, record, columnNames, resultType );
            newResult.add( o );
        }
        return newResult;
    }

}

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
 *
 * <p> ResultSet -&gt; Boolean </p>
 *
 * @author yeweicheng
 * @since 2022-06-11 3:04
 * <br>Now is history!
 */
@Component
public class BooleanResultHandler extends AbstractResultHandler<Boolean, Boolean>{
    @Override
    public Boolean handle(Boolean newResult, ResultSet result, Class resultType) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        ResultSet.Record record = result.rowValues(0);
        ValueWrapper valueWrapper = record.values().get(0);
        return ResultSetUtil.getValue( valueWrapper );
    }
}

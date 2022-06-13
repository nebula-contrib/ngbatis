// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.handler;

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import org.springframework.stereotype.Component;
import ye.weicheng.ngbatis.utils.ResultSetUtil;

import static ye.weicheng.ngbatis.utils.ReflectUtil.castNumber;

/**
 * 结果集数据类型转换器
 * <p> ResultSet -> Number </p>
 *
 * @author yeweicheng
 * @since 2022-06-11 2:08
 * <br>Now is history!
 */
@Component
public class NumberResultHandler extends AbstractResultHandler<Number, Number> {

    @Override
    public Number handle(Number newResult, ResultSet result, Class resultType) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        ResultSet.Record record = result.rowValues(0);
        ValueWrapper valueWrapper = record.values().get(0);
        Number value = ResultSetUtil.getValue( valueWrapper );
        return castNumber( value, resultType );
    }

}

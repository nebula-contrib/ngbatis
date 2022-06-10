// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis;

import com.vesoft.nebula.client.graph.data.ResultSet;
import ye.weicheng.ngbatis.models.MethodModel;

/**
 * 结果解析接口
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public interface ResultResolver {

    <T> T resolve(MethodModel methodModel, ResultSet result );

}

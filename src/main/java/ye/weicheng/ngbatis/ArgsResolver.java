// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis;

import ye.weicheng.ngbatis.models.MethodModel;

import java.util.Map;

/**
 * 参数解析器
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public interface ArgsResolver {

    Map<String, Object> resolve(MethodModel methodModel, Object... args ) ;

}

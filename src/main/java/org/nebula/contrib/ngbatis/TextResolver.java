// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package org.nebula.contrib.ngbatis;

import java.util.Map;


/**
 * 查询语句的中，参数占位符替换所依赖的接口
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public interface TextResolver {

    String resolve(String text, Map<String, Object> args);

}

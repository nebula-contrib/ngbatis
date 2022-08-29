package org.nebula.contrib.ngbatis;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.data.ResultSet;
import org.nebula.contrib.ngbatis.models.MethodModel;

/**
 * 结果解析接口。
 * <p/>
 * @author yeweicheng
 * <br>Now is history!
 */
public interface ResultResolver {

  <T> T resolve(MethodModel methodModel, ResultSet result);

}

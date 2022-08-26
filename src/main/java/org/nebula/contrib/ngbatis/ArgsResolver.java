package org.nebula.contrib.ngbatis;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import java.util.Map;
import org.nebula.contrib.ngbatis.models.MethodModel;

/**.
 * 参数解析器.
 *.
 * @author yeweicheng <br>.
 *     Now is history.
.*/
public interface ArgsResolver {

  Map<String, Object> resolve(MethodModel methodModel, Object... args);
}

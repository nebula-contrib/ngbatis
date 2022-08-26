package org.nebula.contrib.ngbatis;

// Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import java.util.Map;

/**
 * 查询语句的中，参数占位符替换所依赖的接口
 *
 * @author yeweicheng <br>
 *         Now is history!
 */
public interface TextResolver {

    String resolve(String text, Map<String, Object> args);

}

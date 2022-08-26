package org.nebula.contrib.ngbatis.annotations;

// Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 用于处理 Cypher 中不好处理的集合参数，多层 Map 的参数，转换成 一层，以供参数植入的调用
 * </p>
 * <p>
 * 如人参为：
 * </p>
 * <code>
 *       {
 *            entities: [
 *              { name: "name1} ,
 *              { name: "name2" }
 *            ]
 *        }
 *  </code>
 * <p>
 * 则人参将被转换成
 * </p>
 * <code>
 *  {
 *      entities_0_name: "name1",
 *      entities_1_name: "name2"
 *  }
 *  </code>
 *
 * <p>
 * Mapper 文件中的文本，{ entities.name } 也将自动补齐下标位，
 * </p>
 * <p>
 * 并替换成 { entities_0_name }，{ entities_1_name } ... { entities_X_name }
 * </p>
 * <p>
 * 实例可见 neo-mapper\NativeRepository.xml # queryByEntity
 * </p>
 * <p>
 * Mapper 语法模式层使用 ${} 做为占位符，参数层使用 {} 做为占位符。
 * </p>
 *
 * @author yeweicheng <br>
 *         Now is history!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UseKeyArgReplace {
}

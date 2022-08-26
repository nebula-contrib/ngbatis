package org.nebula.contrib.ngbatis.binding;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import com.vesoft.nebula.client.graph.data.ResultSet;
import org.nebula.contrib.ngbatis.ResultHandler;
import org.nebula.contrib.ngbatis.ResultResolver;
import org.nebula.contrib.ngbatis.models.MethodModel;
import org.nebula.contrib.ngbatis.utils.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 默认的结果集处理入口（承担根据不同数据类型与接口返回值类型之间的【路由】角色）
 *
 * @author yeweicheng <br>
 *     Now is history!
 */
@Component
public class DefaultResultResolver implements ResultResolver {

  private Logger log = LoggerFactory.getLogger(DefaultResultResolver.class);

  @Override
  public Object resolve(MethodModel methodModel, ResultSet result) {
    Class returnType = methodModel.getReturnType();
    Class resultType = methodModel.getResultType();

    if (resultType == null) resultType = returnType;

    if (returnType == void.class) return null;

    // 核心方法。获取真正执行结果集类型转换的结果处理执行者
    ResultHandler<Object, Object> handler =
        ResultHandler.getHandler(
            ReflectUtil.sealingBasicType(returnType),
            ReflectUtil.sealingBasicType(resultType));
    log.debug(
        "针对java返回类型 {} 与 xml 返回类型 {} 的结果集处理器为：{}",
        returnType.getName(),
        resultType.getName(),
        handler);

    if (handler == null) {
      return result;
    }
    return handler.handle(returnType, result, resultType);
  }
}

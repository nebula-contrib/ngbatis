package org.nebula.contrib.ngbatis;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.data.ResultSet;
import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.misc.DoubleKeyMap;

/**
 * <p>结果集处理的接口，也是总入口</p> 
 * <p>通过两个接口方法的返回值类型与声明的结果类型获取具体的类型转换实现类。</p>
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public interface ResultHandler<T, Z> {

  DoubleKeyMap<Class, Class, ResultHandler> DIFF_TYPE_RESULT_HANDLER = new DoubleKeyMap<>();
  Map<Class, ResultHandler> HANDLERS = new HashMap<>();

  /**
   * <p>根据接口类型与范型，获取结果集处理策略。</p>
   * @param handlerMap 结果集处理策略的 map
   * @param returnType dao接口返回值类型
   * @return
   */
  static ResultHandler<Object, Object> getHandler(Map<Class, ResultHandler> handlerMap,
      Class returnType) {
    ResultHandler classResultHandler = handlerMap.get(returnType);
    if (classResultHandler == null) {
      Class[] interfaces = returnType.getInterfaces();
      for (Class itf : interfaces) {
        ResultHandler<Object, Object> handler = getHandler(handlerMap, itf);
        if (handler != null) {
          return handler;
        }
      }
      Class superclass = returnType.getSuperclass();

      if (superclass == null) {
        return null;
      } else {
        return getHandler(handlerMap, superclass);
      }
    }
    return classResultHandler;
  }

  /**
   * <p>根据接口类型与范型，获取结果集处理策略。</p>
   * @param returnType dao接口返回值类型
   * @param resultType xml中声明的 resultType，
   *                   当returnType是集合时，为范型。否则与 returnType 相同
   * @return
   */
  static ResultHandler<Object, Object> getHandler(Class returnType, Class resultType) {
    Map<Class, ResultHandler> classResultHandlerMap = DIFF_TYPE_RESULT_HANDLER.get(returnType);
    ResultHandler classResultHandler = null;
    if (classResultHandlerMap != null) {
      classResultHandler = getHandler(classResultHandlerMap, resultType);
    }
    if (classResultHandler == null) {
      Class[] interfaces = returnType.getInterfaces();
      for (Class itf : interfaces) {
        ResultHandler<Object, Object> handler = getHandler(itf, resultType);
        if (handler != null) {
          return handler;
        }
      }
      Class superclass = returnType.getSuperclass();

      if (superclass == null) {
        return null;
      } else {
        return getHandler(superclass, resultType);
      }
    }
    return classResultHandler;
  }

  T handle(Class<T> returnType, ResultSet result, Class resultType);

  T handle(T newResult, ResultSet result, Class resultType)
      throws NoSuchFieldException, IllegalAccessException, InstantiationException;

}

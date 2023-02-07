package org.nebula.contrib.ngbatis.proxy;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.nebula.contrib.ngbatis.models.ClassModel.PROXY_SUFFIX;

import com.vesoft.nebula.client.graph.SessionPool;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.net.Session;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.nebula.contrib.ngbatis.ArgsResolver;
import org.nebula.contrib.ngbatis.Env;
import org.nebula.contrib.ngbatis.ResultResolver;
import org.nebula.contrib.ngbatis.SessionDispatcher;
import org.nebula.contrib.ngbatis.config.NebulaNgbatisConfig;
import org.nebula.contrib.ngbatis.config.ParseCfgProps;
import org.nebula.contrib.ngbatis.exception.QueryException;
import org.nebula.contrib.ngbatis.models.ClassModel;
import org.nebula.contrib.ngbatis.models.MapperContext;
import org.nebula.contrib.ngbatis.models.MethodModel;
import org.nebula.contrib.ngbatis.session.LocalSession;
import org.nebula.contrib.ngbatis.utils.Page;
import org.nebula.contrib.ngbatis.utils.ReflectUtil;
import org.nebula.contrib.ngbatis.utils.ResultSetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 被动态代理类所调用。用于实际的数据库访问并调用结果集处理方法
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class MapperProxy {

  public static Env ENV;
  private static Logger log = LoggerFactory.getLogger(MapperProxy.class);
  @Autowired
  private ParseCfgProps props;
  private ClassModel classModel;

  private Map<String, MethodModel> methodCache = new HashMap<>();


  public MapperProxy(ClassModel classModel) {
    this.classModel = classModel;
    methods(classModel);
  }

  /**
   * <strong>框架中极其重要的方法，被动态代理类所执行。是动态代理的入口方法{@link MapperProxyClassGenerator#method}</strong>
   * 提供给代理类所调用
   *
   * @param className  访问数据库的接口
   * @param methodName 执行数据库操作的方法名
   * @param args     执行数据库操作的参数
   * @return 结果对象映射的 java 对象
   */
  public static Object invoke(String className, String methodName, Object... args) {
    MapperContext mapperContext = ENV.getMapperContext();
    String proxyClassName = className + PROXY_SUFFIX;
    ClassModel classModel = mapperContext.getInterfaces().get(proxyClassName);
    Method method = null;
    if (mapperContext.isResourceRefresh()) {
      try {
        Map<String, ClassModel> classModelMap =
            classModel.getResourceLoader().parseClassModel(classModel.getResource());
        classModel = classModelMap.get(proxyClassName);
        method = classModel.getMethod(methodName).getMethod();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      method = classModel.getMethod(methodName).getMethod();
    }
    return pageSupport(classModel, method, args);
  }

  public static Object invoke(MethodModel methodModel, Object... args) {
    return invoke(null, methodModel, args);
  }

  /**
   * 提供给基类所调用，完整描述整个 orm 流程的核心方法。
   * <ol>
   *   <li>获取方法具体信息，主要包括返回值类型与查询脚本(nGQL)</li>
   *   <li>对上一步获取到的 nGQL 中，参数占位符替换成实际参数值</li>
   *   <li>执行数据库访问</li>
   *   <li>按返回值类型获取对应结果集处理器</li>
   *   <li>完成数据库数据类型向 javaa 对象类型的转化</li>
   * </ol>
   *
   * @param classModel  mapper 接口类型，存放 mapper 标签的属性
   * @param methodModel 接口方法模型，存放了 dao接口的详细信息（nGQL模板、返回值类型等）
   * @param args    执行 nGQL 的参数
   * @return 结果值
   */
  public static Object invoke(ClassModel classModel, MethodModel methodModel, Object... args) {
    Method method = methodModel.getMethod();
    ResultSet query = null;
    // 参数格式转换
    final long step0 = System.currentTimeMillis();
    ArgsResolver argsResolver = ENV.getArgsResolver();
    Map<String, Object> argMap = argsResolver.resolveForTemplate(methodModel, args);
    Map<String, Object> paramWithSchema = new LinkedHashMap<String, Object>(argMap) {{
        put("ng_cm", classModel);
        put("ng_mm", methodModel);
        put("ng_args", args);
      }};
    // beetl 渲染模板
    String textTpl = methodModel.getText();
    String gql = ENV.getTextResolver().resolve(textTpl, paramWithSchema);

    Map<String,Object> parasForDb = argsResolver.resolve(methodModel, args);
    final long step1 = System.currentTimeMillis();
    NebulaNgbatisConfig ngbatisConfig = MapperContext.newInstance().getNgbatisConfig();
    if (ngbatisConfig == null || !ngbatisConfig.getUseSessionPool()) {
      query = executeWithParameter(classModel, methodModel, gql, parasForDb, argMap);
    } else {
      query = executeWithParameterBySessionPool(classModel, methodModel, gql, parasForDb, argMap);
    }

    final long step2 = System.currentTimeMillis();
    if (!query.isSucceeded()) {
      throw new QueryException("数据查询失败：" + query.getErrorMessage());
    }

    if (methodModel.getResultType() == ResultSet.class) {
      return query;
    }

    ResultResolver resultResolver = ENV.getResultResolver();
    Object resolve = resultResolver.resolve(methodModel, query);
    long step3 = System.currentTimeMillis();

    log.debug("nGql make up costs {}ms, query costs {}ms, result handle costs {}ms ",
        step1 - step0, step2 - step1, step3 - step2);
    return resolve;
  }

  public Object invoke(Method method, Object... args) {
    return invoke(null, method, args);
  }

  /**
   * 执行gql的方法
   * @param classModel 接口类模型
   * @param method 接口方法模型
   * @param args dao接口的参数
   * @return gql直接结果的返回值
   */
  public Object invoke(ClassModel classModel, Method method, Object... args) {
    MethodModel methodModel = methodCache.get(method.getName());
    methodModel.setMethod(method);

    return invoke(classModel, methodModel, args);
  }

  /**
   * 自动对该分页的接口进行分页操作<br/> 该分页：接口参数中有 {@link Page Page} 对象。
   *
   * @param classModel 应用初始化后，数据访问接口对应的类模型
   * @param method   执行数据库操作的方法
   * @param args     执行数据库操作的参数
   * @return 结果对象映射的 java 对象
   */
  private static Object pageSupport(ClassModel classModel, Method method, Object[] args) {
    int pageParamIndex = ReflectUtil.containsType(method, Page.class);

    MapperProxy mapperProxy = new MapperProxy(classModel);
    if (pageParamIndex < 0) {
      return mapperProxy.invoke(classModel, method, args);
    }

    String countMethodName = method.getName() + "$Count";
    String pageMethodName = method.getName() + "$Page";

    Long count = (Long) mapperProxy
        .invoke(classModel, classModel.getMethods().get(countMethodName), args);
    List rows = (List) mapperProxy
        .invoke(classModel, classModel.getMethods().get(pageMethodName), args);

    Page page = (Page) args[pageParamIndex];
    page.setTotal(count);
    page.setRows(rows);
    return rows;
  }

  /**
   * 通过 nebula-graph 客户端执行数据库访问。被 invoke 所调用，间接为动态代理类服务。
   *
   * @param gql  待执行的查询脚本（模板）
   * @param params 待执行脚本的参数所需的参数
   * @return nebula-graph 的未被 orm 操作的原始结果集
   */
  public static ResultSet executeWithParameter(ClassModel cm, MethodModel mm, String gql,
      Map<String, Object> params, Map<String, Object> paramsForTemplate) {
    LocalSession localSession = null;
    Session session = null;
    ResultSet result = null;
    String proxyClass = null;
    String proxyMethod = null;
    String localSessionSpace = null;
    String autoSwitch = null;
    SessionDispatcher dispatcher = ENV.getDispatcher();
    try {
      localSession = dispatcher.poll();
      if (log.isDebugEnabled()) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[6];
        proxyClass = stackTraceElement.getClassName();
        proxyMethod = stackTraceElement.getMethodName();
        localSessionSpace = localSession.getCurrentSpace();
      }

      String currentSpace = getSpace(cm, mm);
      String[] qlAndSpace = qlWithSpace(localSession, gql, currentSpace);
      gql = qlAndSpace[1];
      autoSwitch = qlAndSpace[0] == null ? "" : qlAndSpace[0];
      session = localSession.getSession();
      result = session.executeWithParameter(gql, params);
      localSession.setCurrentSpace(result.getSpaceName());
      if (result.isSucceeded()) {
        return result;
      } else {
        throw new QueryException(" 数据查询失败" + result.getErrorMessage());
      }
    } catch (Exception e) {
      throw new QueryException("数据查询失败：" + e.getMessage(), e);
    } finally {
      if (log.isDebugEnabled()) {
        log.debug("\n\t- proxyMethod: {}#{}"
                + "\n\t- session space: {}"
                + (isEmpty(autoSwitch) ? "{}" : "\n\t- auto switch to: {}")
                + "\n\t- nGql：{}"
                + "\n\t- params: {}"
                + "\n\t- result：{}",
            proxyClass, proxyMethod, localSessionSpace, autoSwitch, gql, paramsForTemplate, result);
      }
      handleSession(dispatcher, localSession, result);
    }
  }

  /**
   * 通过 nebula-graph 客户端执行数据库访问。被 invoke 所调用，间接为动态代理类服务。
   *
   * @param gql  待执行的查询脚本（模板）
   * @param params 待执行脚本的参数所需的参数
   * @return nebula-graph 的未被 orm 操作的原始结果集
   */
  public static ResultSet executeWithParameterBySessionPool(ClassModel cm, MethodModel mm, String gql,
      Map<String, Object> params, Map<String, Object> paramsForTemplate) {

    ResultSet result = null;
    String proxyClass = null;
    String proxyMethod = null;
    String currentSpace = null;

    try {
      if (log.isDebugEnabled()) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[6];
        proxyClass = stackTraceElement.getClassName();
        proxyMethod = stackTraceElement.getMethodName();
      }

      currentSpace = getSpace(cm, mm);
      SessionPool sessionPool = ENV.getSessionPool(currentSpace);
      if (sessionPool == null) {
        throw new QueryException(currentSpace + " sessionPool is null");
      }
      result = sessionPool.execute(gql, params);
      if (result.isSucceeded()) {
        return result;
      } else {
        throw new QueryException(" ResultSet error: " + result.getErrorMessage());
      }
    } catch (Exception e) {
      throw new QueryException("execute failed: " + e.getMessage(), e);
    } finally {
      if (log.isDebugEnabled()) {
        log.debug("\n\t- proxyMethod: {}#{}"
                + "\n\t- session space: {}"
                + "\n\t- nGql：{}"
                + "\n\t- params: {}"
                + "\n\t- result：{}",
            proxyClass, proxyMethod, currentSpace, gql, paramsForTemplate, result);
      }
    }
  }

  private static void handleSession(SessionDispatcher dispatcher,
      LocalSession localSession, ResultSet result) {
    if (localSession != null) {
      boolean sessionError = ResultSetUtil.isSessionError(result);
      if (sessionError || dispatcher.timeToRelease(localSession)) {
        dispatcher.release(localSession);
      } else {
        dispatcher.offer(localSession);
      }
    }
  }

  private static String[] qlWithSpace(LocalSession localSession, String gql, String currentSpace)
      throws IOErrorException {
    String[] qlAndSpace = new String[2];
    gql = gql.trim();
    String sessionSpace = localSession.getCurrentSpace();
    boolean sameSpace = Objects.equals(sessionSpace, currentSpace);
    if (!sameSpace) {
      qlAndSpace[0] = currentSpace;
      Session session = localSession.getSession();
      session.execute(String.format("USE %s", currentSpace));
    }
    qlAndSpace[1] = String.format("\n\t\t%s", gql);
    return qlAndSpace;
  }

  /**
   * 获取当前语句所执行的目标space。
   * @param cm 当前接口的类模型
   * @param mm 当前接口方法的方法模型
   * @return 目标space
   */
  public static String getSpace(ClassModel cm, MethodModel mm) {
    return mm != null && mm.getSpace() != null ? mm.getSpace()
      : cm != null && cm.getSpace() != null ? cm.getSpace()
        : ENV.getSpace();
  }

  public static Logger getLog() {
    return log;
  }

  public static void setLog(Logger log) {
    MapperProxy.log = log;
  }

  private void methods(ClassModel classModel) {
    methodCache.clear();
    Map<String, MethodModel> methods = classModel.getMethods();
    methodCache.putAll(methods);
  }

  public ParseCfgProps getProps() {
    return props;
  }

  public void setProps(ParseCfgProps props) {
    this.props = props;
  }

  public ClassModel getClassModel() {
    return classModel;
  }

  public void setClassModel(ClassModel classModel) {
    this.classModel = classModel;
  }

  public Map<String, MethodModel> getMethodCache() {
    return methodCache;
  }

  public void setMethodCache(Map<String, MethodModel> methodCache) {
    this.methodCache = methodCache;
  }
}

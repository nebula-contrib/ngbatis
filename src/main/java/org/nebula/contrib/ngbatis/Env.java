package org.nebula.contrib.ngbatis;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.alibaba.fastjson.parser.ParserConfig;
import com.vesoft.nebula.client.graph.SessionPool;
import com.vesoft.nebula.client.graph.net.Session;
import org.nebula.contrib.ngbatis.base.GraphBaseExt;
import org.nebula.contrib.ngbatis.config.ParseCfgProps;
import org.nebula.contrib.ngbatis.models.MapperContext;
import org.nebula.contrib.ngbatis.proxy.MapperProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * 当前框架的全局环境信息，用于指定各个重要环节所使用的具体实现类
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class Env {

  public static ClassLoader classLoader;

  // 使用 fastjson 安全模式，规避任意代码执行风险
  static {
    ParserConfig.getGlobalInstance().setSafeMode(true);
  }

  private Logger log = LoggerFactory.getLogger(Env.class);

  //  private SessionFactory sessionFactory;
  private TextResolver textResolver;
  private ResultResolver resultResolver;
  private ArgsResolver argsResolver;
  private ArgNameFormatter argNameFormatter;
  private ParseCfgProps cfgProps;
  private ApplicationContext context;

  private String username;
  private String password;
  private boolean reconnect = false;
  private String space;
  private PkGenerator pkGenerator;

  private SessionDispatcher dispatcher;
  private MapperContext mapperContext;

  public Env() {
  }

  /**
   * 构建 ngbatis 环境信息。
   * @param textResolver 模板引擎。默认 beetl
   * @param resultResolver 结果集路由。
   * @param argsResolver 参数解析器
   * @param argNameFormatter 参数名格式化器
   * @param cfgProps 解析的参数配置
   * @param applicationContext 应用上下文
   * @param username nebula graph 用户名
   * @param password nebula graph 密码
   * @param reconnect nebula graph 连接是否支持重连
   * @param space  nebula graph 数据空间
   * @param pkGenerator 主键生成器
   * @param dispatcher 本地会话调度器
   */
  public Env(
      TextResolver textResolver,
      ResultResolver resultResolver, ArgsResolver argsResolver,
      ArgNameFormatter argNameFormatter, ParseCfgProps cfgProps,
      ApplicationContext applicationContext,
      String username, String password, boolean reconnect, String space,
      PkGenerator pkGenerator, SessionDispatcher dispatcher
  ) {
    this.textResolver = textResolver;
    this.resultResolver = resultResolver;
    this.argsResolver = argsResolver;
    this.argNameFormatter = argNameFormatter;
    this.cfgProps = cfgProps;
    this.context = applicationContext;
    this.username = username;
    this.password = password;
    this.reconnect = reconnect;
    this.space = space;
    this.pkGenerator = pkGenerator;
    this.mapperContext = MapperContext.newInstance();
    MapperProxy.ENV = this;
    GraphBaseExt.ENV = this;
    this.dispatcher = dispatcher;
    log.debug(" Env constructor ");
  }

  public SessionDispatcher getDispatcher() {
    return dispatcher;
  }

  /**
   * 获取 Nebula SessionPool
   * @return SessionPool
   */
  public SessionPool getSessionPool(String spaceName) {
    return mapperContext.getNebulaSessionPoolMap().get(spaceName);
  }

  /**
   * <p>获取nebula graph的会话。</p>
   * @return session
   */
  public Session openSession() {
    try {
      return mapperContext.getNebulaPool().getSession(username, password, reconnect);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isReconnect() {
    return reconnect;
  }

  public void setReconnect(boolean reconnect) {
    this.reconnect = reconnect;
  }

  public Logger getLog() {
    return log;
  }

  public void setLog(Logger log) {
    this.log = log;
  }

  public TextResolver getTextResolver() {
    return textResolver;
  }

  public void setTextResolver(TextResolver textResolver) {
    this.textResolver = textResolver;
  }

  public ResultResolver getResultResolver() {
    return resultResolver;
  }

  public void setResultResolver(ResultResolver resultResolver) {
    this.resultResolver = resultResolver;
  }

  public ArgsResolver getArgsResolver() {
    return argsResolver;
  }

  public void setArgsResolver(ArgsResolver argsResolver) {
    this.argsResolver = argsResolver;
  }

  public ArgNameFormatter getArgNameFormatter() {
    return argNameFormatter;
  }

  public void setArgNameFormatter(ArgNameFormatter argNameFormatter) {
    this.argNameFormatter = argNameFormatter;
  }

  public ParseCfgProps getCfgProps() {
    return cfgProps;
  }

  public void setCfgProps(ParseCfgProps cfgProps) {
    this.cfgProps = cfgProps;
  }

  public ApplicationContext getContext() {
    return context;
  }

  public void setContext(ApplicationContext context) {
    this.context = context;
  }

  public String getSpace() {
    return space;
  }

  public void setSpace(String space) {
    this.space = space;
  }

  public MapperContext getMapperContext() {
    return mapperContext;
  }

  public void setMapperContext(MapperContext mapperContext) {
    this.mapperContext = mapperContext;
  }

  public PkGenerator getPkGenerator() {
    return pkGenerator;
  }

  public void setPkGenerator(PkGenerator pkGenerator) {
    this.pkGenerator = pkGenerator;
  }
}

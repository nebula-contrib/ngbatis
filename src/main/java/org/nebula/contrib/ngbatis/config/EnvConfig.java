package org.nebula.contrib.ngbatis.config;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import org.nebula.contrib.ngbatis.*;
import org.nebula.contrib.ngbatis.models.MapperContext;
import org.nebula.contrib.ngbatis.session.IntervalCheckSessionDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**.
 * 环境配置类，用于存放框架的配置信息.
 *.
 * @author yeweicheng <br>.
 *     Now is history.
.*/
@Configuration
public class EnvConfig {

  public static boolean reconnect = true;

  /** xml 所采用的模板引擎策略.*/
  @Autowired private TextResolver textResolver;
  /** 结果处理路由策略.*/
  @Autowired private ResultResolver resultResolver;
  /** 执行到数据库前的参数替换策略.*/
  @Autowired private ArgsResolver argsResolver;
  /** 复杂对象，转换成 xml 可访问的参数名格式化策略.*/
  @Autowired private ArgNameFormatter argNameFormatter;
  /** mapper.xml 解析时的配置，及声明 xml 标签用途跟 xml文件存放位置的配置..*/
  @Autowired private ParseCfgProps cfgProps;
  /** Springboot 应用上下文.*/
  @Autowired private ApplicationContext context;
  /** Nebula 连接配置.*/
  @Autowired private NebulaJdbcProperties properties;
  /** 主键生成器.*/
  @Autowired(required = false)
  private PkGenerator pkGenerator;

  private SessionDispatcher sessionDispatcher;

  @Bean
  public Env getEnv() {
    sessionDispatcher =
      new IntervalCheckSessionDispatcher(
        MapperContext.newInstance().getNebulaPoolConfig());
    return new Env(
      textResolver,
      resultResolver,
      argsResolver,
      argNameFormatter,
      cfgProps,
      context,
      properties.getUsername(),
      properties.getPassword(),
      reconnect,
      properties.getSpace(),
      pkGenerator,
      sessionDispatcher);
  }

  public TextResolver getTextResolver() {
    return textResolver;
  }

  public void setTextResolver(final TextResolver textResolver) {
    this.textResolver = textResolver;
  }

  public ResultResolver getResultResolver() {
    return resultResolver;
  }

  public void setResultResolver(final ResultResolver resultResolver) {
    this.resultResolver = resultResolver;
  }

  public ArgsResolver getArgsResolver() {
    return argsResolver;
  }

  public void setArgsResolver(final ArgsResolver argsResolver) {
    this.argsResolver = argsResolver;
  }

  public ArgNameFormatter getArgNameFormatter() {
    return argNameFormatter;
  }

  public void setArgNameFormatter(final ArgNameFormatter argNameFormatter) {
    this.argNameFormatter = argNameFormatter;
  }

  public ParseCfgProps getCfgProps() {
    return cfgProps;
  }

  public void setCfgProps(final ParseCfgProps cfgProps) {
    this.cfgProps = cfgProps;
  }

  public ApplicationContext getContext() {
    return context;
  }

  public void setContext(final ApplicationContext context) {
    this.context = context;
  }

  public NebulaJdbcProperties getProperties() {
    return properties;
  }

  public void setProperties(final NebulaJdbcProperties properties) {
    this.properties = properties;
  }
}

package org.nebula.contrib.ngbatis;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.utils.ConfigUtil.getConfig;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.CASignedSSLParam;
import com.vesoft.nebula.client.graph.data.SSLParam;
import com.vesoft.nebula.client.graph.data.SSLParam.SignMode;
import java.util.Map;
import org.nebula.contrib.ngbatis.config.NebulaJdbcProperties;
import org.nebula.contrib.ngbatis.config.NgbatisConfig;
import org.nebula.contrib.ngbatis.config.ParseCfgProps;
import org.nebula.contrib.ngbatis.models.ext.SettableCASignedSSLParam;
import org.nebula.contrib.ngbatis.models.ext.SettableSelfSignedSSLParam;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;

/**
 * SpringBoot start <br> 用于被 SpringBoot 所引导启动。
 *
 * @author yeweicheng
 * @since 2022-06-17 10:01
 * <br>Now is history!
 */
public class NgbatisContextInitializer implements ApplicationContextInitializer {

  @Override
  public void initialize(ConfigurableApplicationContext context) {

    Env.classLoader = context.getClassLoader();

    ConfigurableEnvironment environment = context.getEnvironment();

    NebulaPoolConfig nebulaPool = getNebulaPoolConfig(environment);

    NgbatisConfig ngbatisConfig = getNebulaNgbatisConfig(environment);

    // @author: Szt-1 fix #https://github.com/nebula-contrib/ngbatis/pull/54
    if (environment.getProperty("nebula.hosts") != null) {
      NebulaJdbcProperties nebulaJdbcProperties =
              getNebulaJdbcProperties(environment)
                      .setPoolConfig(nebulaPool)
                      .setNgbatis(ngbatisConfig);

      ParseCfgProps parseCfgProps = readParseCfgProps(environment);

      context.addBeanFactoryPostProcessor(
              new NgbatisBeanFactoryPostProcessor(nebulaJdbcProperties, parseCfgProps, context)
      );

    }
  }

  private ParseCfgProps readParseCfgProps(ConfigurableEnvironment environment) {
    return new ParseCfgProps().setId(environment.getProperty("cql.parser.id"))
      .setLogShow(environment.getProperty("cql.parser.log-show"))
      .setMapper(environment.getProperty("cql.parser.mapper"))
      .setNamespace(environment.getProperty("cql.parser.namespace"))
      .setMapperLocations(environment.getProperty("cql.parser.mapper-locations"))
      .setMapperTplLocation(environment.getProperty("cql.parser.mapper-tpl-location"))
      .setResultType(environment.getProperty("cql.parser.result-type"))
      .setParameterType(environment.getProperty("cql.parser.parameter-type"))
      .setStatementEnd(environment.getProperty("cql.parser.statement-end"))
      .setStatementStart(environment.getProperty("cql.parser.statement-start"))
      .setResourceRefresh(
        environment.getProperty("cql.parser.statement-start", Boolean.class)
      );
  }

  private NebulaJdbcProperties getNebulaJdbcProperties(ConfigurableEnvironment environment) {
    NebulaJdbcProperties nebulaJdbcProperties = new NebulaJdbcProperties();
    String hosts = getProperty(environment, "nebula.hosts");
    String username = getProperty(environment, "nebula.username");
    String password = getProperty(environment, "nebula.password");
    String space = getProperty(environment, "nebula.space");
    return nebulaJdbcProperties
      .setHosts(hosts)
      .setUsername(username)
      .setPassword(password)
      .setSpace(space);
  }
  
  // optimize: https://github.com/nebula-contrib/ngbatis/issues/69
  // issue contributor @llinzhe
  private String getProperty(ConfigurableEnvironment environment, String key) {
    String property = environment.getProperty(key);
    Assert.notNull(property, String.format("Configuration Item: [ %s ] can not be null.", key));
    return property;
  }

  @SuppressWarnings("unchecked")
  private NebulaPoolConfig getNebulaPoolConfig(ConfigurableEnvironment environment) {
    NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig()
        .setMinConnSize(
        environment.getProperty("nebula.pool-config.min-conns-size", Integer.class, 0))
        .setMaxConnSize(
        environment.getProperty("nebula.pool-config.max-conns-size", Integer.class, 10))
        .setTimeout(environment.getProperty("nebula.pool-config.timeout", Integer.class, 0))
        .setIdleTime(environment.getProperty("nebula.pool-config.idle-time", Integer.class, 0))
        .setIntervalIdle(
        environment.getProperty("nebula.pool-config.interval-idle", Integer.class, -1))
        .setUseHttp2(environment.getProperty("nebula.pool-config.use-http2", Boolean.class, false))
        .setCustomHeaders(
          getConfig(environment, "nebula.pool-config.custom-headers", Map.class)
        )
        .setWaitTime(environment.getProperty("nebula.pool-config.wait-time", Integer.class, 0));
    confSsl(environment, nebulaPoolConfig);
    return nebulaPoolConfig;
  }

  private void confSsl(
      ConfigurableEnvironment environment,
      NebulaPoolConfig nebulaPoolConfig
  ) {
    boolean enableSsl = environment.getProperty("nebula.pool-config.enable-ssl", Boolean.class, false);
    nebulaPoolConfig.setEnableSsl(enableSsl);
    if (enableSsl) {
      SignMode signMode = environment.getProperty(
        "nebula.pool-config.ssl-param.sign-mode",
        SignMode.class,
        SignMode.SELF_SIGNED
      );
      
      if (signMode == SignMode.NONE) {
        return;
      }

      Class<? extends SSLParam> sslParamClass = signMode == SignMode.SELF_SIGNED
        ? SettableSelfSignedSSLParam.class
        : SettableCASignedSSLParam.class;
      
      SSLParam sslParam = getConfig(
        environment, 
        "nebula.pool-config.ssl-param",
        sslParamClass
      );
      
      if (sslParam == null) {
        nebulaPoolConfig.setSslParam(new CASignedSSLParam());
        return;
      }
      nebulaPoolConfig.setSslParam(sslParam);
    }
  }
  
  /**
   * 获取 ngbatis 自定义配置
   */
  private NgbatisConfig getNebulaNgbatisConfig(ConfigurableEnvironment environment) {
    return new NgbatisConfig()
            .setSessionLifeLength(
              environment.getProperty("nebula.ngbatis.session-life-length", Long.class)
            )
            .setCheckFixedRate(
              environment.getProperty("nebula.ngbatis.check-fixed-rate", Long.class)
            )
            .setUseSessionPool(
              environment.getProperty("nebula.ngbatis.use-session-pool", Boolean.class)
            );
  }

}

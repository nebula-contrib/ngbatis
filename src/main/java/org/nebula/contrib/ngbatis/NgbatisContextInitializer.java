package org.nebula.contrib.ngbatis;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import java.util.Objects;
import org.nebula.contrib.ngbatis.config.NebulaJdbcProperties;
import org.nebula.contrib.ngbatis.config.ParseCfgProps;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

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

    if (environment.getProperty("nebula.hosts")!=null){

      NebulaJdbcProperties nebulaJdbcProperties =
              getNebulaJdbcProperties(environment)
                      .setPoolConfig(nebulaPool);

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
        environment.getProperty("cql.parser.statement-start", Boolean.class));
  }

  private NebulaJdbcProperties getNebulaJdbcProperties(ConfigurableEnvironment environment) {
    NebulaJdbcProperties nebulaJdbcProperties = new NebulaJdbcProperties();
    return nebulaJdbcProperties
      .setHosts(Objects.requireNonNull(environment.getProperty("nebula.hosts")))
      .setUsername(environment.getProperty("nebula.username"))
      .setPassword(environment.getProperty("nebula.password"))
      .setSpace(environment.getProperty("nebula.space"));
  }

  private NebulaPoolConfig getNebulaPoolConfig(ConfigurableEnvironment environment) {
    NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig()
        .setMinConnSize(
        environment.getProperty("nebula.pool-config.min-conn-size", Integer.class, 0))
        .setMaxConnSize(
        environment.getProperty("nebula.pool-config.max-conn-size", Integer.class, 10))
        .setTimeout(environment.getProperty("nebula.pool-config.timeout", Integer.class, 0))
        .setIdleTime(environment.getProperty("nebula.pool-config.idle-time", Integer.class, 0))
        .setIntervalIdle(
        environment.getProperty("nebula.pool-config.interval-idle", Integer.class, -1))
        .setWaitTime(environment.getProperty("nebula.pool-config.wait-time", Integer.class, 0));
    // TODO enable ssl
    return nebulaPoolConfig;
  }


}

// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package org.nebula.contrib.ngbatis;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.nebula.contrib.ngbatis.config.NebulaJdbcProperties;
import org.nebula.contrib.ngbatis.config.ParseCfgProps;
import org.nebula.contrib.ngbatis.io.DaoResourceLoader;
import org.nebula.contrib.ngbatis.models.ClassModel;
import org.nebula.contrib.ngbatis.models.MapperContext;
import org.nebula.contrib.ngbatis.proxy.MapperProxyClassGenerator;
import org.nebula.contrib.ngbatis.proxy.RAMClassLoader;

import java.lang.annotation.Annotation;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static org.nebula.contrib.ngbatis.models.ClassModel.PROXY_SUFFIX;
import static org.nebula.contrib.ngbatis.proxy.NebulaDaoBasicExt.*;

/**
 * SpringBoot start <br>
 *     用于被 SpringBoot 所引导启动。
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

        NebulaPoolConfig nebulaPool = getNebulaPoolConfig( environment );

        NebulaJdbcProperties nebulaJdbcProperties =
                getNebulaJdbcProperties( environment )
                        .setPoolConfig( nebulaPool );

        ParseCfgProps parseCfgProps = readParseCfgProps( environment );

        context.addBeanFactoryPostProcessor(new NgbatisBeanFactoryPostProcessor( nebulaJdbcProperties, parseCfgProps, context ));

    }

    private ParseCfgProps readParseCfgProps(ConfigurableEnvironment environment) {
        return  new ParseCfgProps().setId(environment.getProperty( "cql.parser.id" ))
                .setLogShow( environment.getProperty( "cql.parser.log-show" ) )
                .setMapper( environment.getProperty( "cql.parser.mapper" ) )
                .setNamespace( environment.getProperty( "cql.parser.namespace" ) )
                .setMapperLocations( environment.getProperty( "cql.parser.mapper-locations" ) )
                .setMapperTplLocation( environment.getProperty( "cql.parser.mapper-tpl-location" ) )
                .setResultType( environment.getProperty( "cql.parser.result-type" ) )
                .setParameterType( environment.getProperty( "cql.parser.parameter-type" ) )
                .setStatementEnd( environment.getProperty( "cql.parser.statement-end" ) )
                .setStatementStart( environment.getProperty( "cql.parser.statement-start" ) )
                .setResourceRefresh( environment.getProperty( "cql.parser.statement-start", Boolean.class ) );
    }

    private NebulaJdbcProperties getNebulaJdbcProperties(ConfigurableEnvironment environment) {
        NebulaJdbcProperties nebulaJdbcProperties = new NebulaJdbcProperties();
        return nebulaJdbcProperties.setHosts(Objects.requireNonNull(environment.getProperty("nebula.hosts")))
                .setUsername( environment.getProperty("nebula.username" ))
                .setPassword( environment.getProperty("nebula.password" ) )
                .setSpace( environment.getProperty( "nebula.space" ) );
    }

    private NebulaPoolConfig getNebulaPoolConfig(ConfigurableEnvironment environment) {
        NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig()
                .setMinConnSize( environment.getProperty( "nebula.pool-config.min-conn-size", Integer.class, 0 ) )
                .setMaxConnSize( environment.getProperty( "nebula.pool-config.max-conn-size", Integer.class, 10 ) )
                .setTimeout( environment.getProperty( "nebula.pool-config.timeout", Integer.class, 0 ) )
                .setIdleTime( environment.getProperty( "nebula.pool-config.idle-time", Integer.class, 0 ) )
                .setIntervalIdle( environment.getProperty( "nebula.pool-config.interval-idle", Integer.class, -1 ) )
                .setWaitTime( environment.getProperty( "nebula.pool-config.wait-time", Integer.class, 0 ) );
//                .setMinClusterHealthRate( environment.getProperty( "nebula.pool-config.min-cluster-health-rate", Double.class, 1.0D ) );
        // TODO enable ssl
        return nebulaPoolConfig;
    }


}

/**
 * Ngbatis 创建动态代理的主程
 *
 * @author yeweicheng
 * @since 2022-06-17 10:01
 * <br>Now is history!
 */
class NgbatisBeanFactoryPostProcessor implements BeanFactoryPostProcessor , Ordered {

    private Logger log = LoggerFactory.getLogger( NgbatisBeanFactoryPostProcessor.class );
    private NebulaJdbcProperties nebulaJdbcProperties;
    private ParseCfgProps parseCfgProps;
    private ConfigurableApplicationContext context;
    private MapperProxyClassGenerator beanFactory = new MapperProxyClassGenerator();
    public NgbatisBeanFactoryPostProcessor(NebulaJdbcProperties nebulaJdbcProperties, ParseCfgProps parseCfgProps, ConfigurableApplicationContext context) {
        this.nebulaJdbcProperties = nebulaJdbcProperties;
        this.parseCfgProps = parseCfgProps;
        this.context = context;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        NebulaPool nebulaPool = nebulaPool();
        mapperContext(nebulaPool);
    }

    public MapperContext mapperContext( NebulaPool nebulaPool ) {
        DaoResourceLoader daoBasicResourceLoader = new DaoResourceLoader( parseCfgProps );
        MapperContext context =  MapperContext.newInstance();
        context.setResourceRefresh( parseCfgProps.isResourceRefresh() );
        Map<String, ClassModel> interfaces = daoBasicResourceLoader.load();
        Map<String, String> daoBasicTpl = daoBasicResourceLoader.loadTpl();
        context.setDaoBasicTpl( daoBasicTpl );
        context.setNebulaPool( nebulaPool );
        context.setInterfaces( interfaces );
        figureTagTypeMapping(  interfaces.values() , context.getTagTypeMapping() );

        registerBean( context );
        return context;
    }

    /**
     * 自动从代码中获取 实体类与数据库标签 的映射关系
     * @param classModels 类模型
     * @param tagTypeMapping 实体类与数据库标签 （容器）
     */
    private void figureTagTypeMapping(
            Collection<ClassModel> classModels,
            Map<String, Class<?>> tagTypeMapping ) {

        for (ClassModel classModel : classModels) {
            Class<?>[] entityTypeAndIdType = entityTypeAndIdType(classModel.getNamespace());
            if( entityTypeAndIdType != null ) {
                Class<?> entityType = entityTypeAndIdType[0];
                String vertexName = vertexName(entityType);
                tagTypeMapping.putIfAbsent( vertexName, entityType );
            }
        }

    }

    /**
     * 为所有的动态代理类 注册Bean到SpringBoot
     * @param context Ngbatis上下文
     */
    private void registerBean(MapperContext context)  {
        Map<String, ClassModel> interfaces = context.getInterfaces();
        for( ClassModel cm : interfaces.values() ) {
            beanFactory.setClassCode(cm);
        }
        RAMClassLoader ramClassLoader = new RAMClassLoader( context.getInterfaces() );
        for( ClassModel cm : interfaces.values() ) {
            try {
                String className = cm.getNamespace().getName() + PROXY_SUFFIX;
                registerBean( cm, ramClassLoader.loadClass( className ) );
                log.info( "Bean had been registed  (代理类注册成bean): {}" , className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 为单个动态代理类 注册Bean到SpringBoot
     * @param cm 类模型
     * @param proxy 动态代理类
     */
    private void registerBean( ClassModel cm, Class proxy  ) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(proxy);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        registerBean( getBeanName( cm ) + PROXY_SUFFIX , beanDefinition );
    }

    /**
     * 为所代理的类指定 Bean 名
     * @param className 类名
     * @param beanDefinition Spring 的bean注册器
     */
    private void registerBean( String className, BeanDefinition beanDefinition ) {
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();
        beanFactory.registerBeanDefinition(className, beanDefinition);
    }

    /**
     * 获取 Bean 的名字
     * @param cm
     * @return
     */
    private String getBeanName( ClassModel cm ) {
        Annotation annotation = cm.getNamespace().getAnnotation(Component.class);
        if( annotation == null ) {
            return cm.getNamespace().getSimpleName();
        } else {
            return ((Component)annotation).value();
        }
    }


    /**
     * 创建 Nebula 连接池
     * @return Nebula 连接池
     */
    public NebulaPool nebulaPool() {
        NebulaPool pool = new NebulaPool();
        try {
            pool.init(nebulaJdbcProperties.getHostAddresses(), nebulaJdbcProperties.getPoolConfig());
        } catch (UnknownHostException e) {
            throw new RuntimeException("Can not connect to Nebula Graph");
        }
        return pool;
    }

    @Override
    public int getOrder() {
        return 100;
    }
}

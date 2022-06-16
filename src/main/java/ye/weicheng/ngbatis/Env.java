// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis;

import com.alibaba.fastjson.parser.ParserConfig;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import org.springframework.core.annotation.Order;
import ye.weicheng.ngbatis.annotations.TimeLog;
import ye.weicheng.ngbatis.config.ParseCfgProps;
import ye.weicheng.ngbatis.models.ClassModel;
import ye.weicheng.ngbatis.models.MapperContext;
import ye.weicheng.ngbatis.proxy.MapperProxy;
import ye.weicheng.ngbatis.proxy.RAMClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

import static ye.weicheng.ngbatis.models.ClassModel.PROXY_SUFFIX;

/**
 * 当前框架的全局环境信息，用于指定各个重要环节所使用的具体实现类
 *
 * @author yeweicheng
 * <br>Now is history!
 */
@Component
@Order(0)
public class Env {

    // 使用 fastjson 安全模式，规避任意代码执行风险
    static {
        ParserConfig.getGlobalInstance().setSafeMode(true);
    }

    public static ClassLoader classLoader;

    private Logger log = LoggerFactory.getLogger( Env.class );

    private ResourceLoader resourceLoader;
    private NebulaPool nebulaPool;
//    private SessionFactory sessionFactory;
    private TextResolver textResolver;
    private ResultResolver resultResolver;
    private ArgsResolver argsResolver;
    private BeanFactory beanFactory;
    private ArgNameFormatter argNameFormatter;
    private ParseCfgProps cfgProps;
    private ApplicationContext context;

    private String username;
    private String password;
    private boolean reconnect = false;
    private String space;
    private PkGenerator pkGenerator;


    public Env() {}

    private MapperContext mapperContext;

    public Env(
            ResourceLoader resourceLoader, NebulaPool nebulaPool, TextResolver textResolver,
            ResultResolver resultResolver, ArgsResolver argsResolver, BeanFactory beanFactory,
            ArgNameFormatter argNameFormatter, ParseCfgProps cfgProps, ApplicationContext applicationContext,
            String username, String password, boolean reconnect, String space,
            PkGenerator pkGenerator
    ) {
        this.resourceLoader = resourceLoader;
        this.nebulaPool = nebulaPool;
        this.textResolver = textResolver;
        this.resultResolver = resultResolver;
        this.argsResolver = argsResolver;
        this.beanFactory = beanFactory;
        this.argNameFormatter = argNameFormatter;
        this.cfgProps = cfgProps;
        this.context = applicationContext;
        this.mapperContext = mapperContext();
        this.username = username;
        this.password = password;
        this.reconnect = reconnect;
        this.space = space;
        this.pkGenerator = pkGenerator;
        log.debug( " Env constructor ");
    }

    public Session openSession() {
        try {
            return nebulaPool.getSession( username, password, reconnect );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public MapperContext mapperContext() {
        MapperContext context = new MapperContext();
        context.setResourceRefresh( cfgProps.isResourceRefresh() );
        Map<String, ClassModel> interfaces = resourceLoader.load();
        context.setNebulaPool( nebulaPool );
        context.setInterfaces( interfaces );
        registerBean( context );
        MapperProxy.ENV = this;
        return context;
    }

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
                log.debug( "bean had been register: {}" , className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void registerBean( ClassModel cm, Class proxy  ) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(proxy);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();

        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();
        beanFactory.registerBeanDefinition(getBeanName( cm ) + PROXY_SUFFIX, beanDefinition);
    }

    private String getBeanName( ClassModel cm ) {
        Annotation annotation = cm.getNamespace().getAnnotation(Component.class);
        if( annotation == null ) {
            return cm.getNamespace().getSimpleName();
        } else {
            return ((Component)annotation).value();
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

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public NebulaPool getNebulaPool() {
        return nebulaPool;
    }

    public void setNebulaPool(NebulaPool nebulaPool) {
        this.nebulaPool = nebulaPool;
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

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
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

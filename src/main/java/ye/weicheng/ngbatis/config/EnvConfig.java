// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.config;

import com.vesoft.nebula.client.graph.net.NebulaPool;
import ye.weicheng.ngbatis.*;
import ye.weicheng.ngbatis.models.MapperContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;

/**
 * 环境配置类，用于存放框架的配置信息
 *
 * @author yeweicheng
 * <br>Now is history!
 */
@Configuration
public class EnvConfig {

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private TextResolver textResolver;
    @Autowired
    private ResultResolver resultResolver;
    @Autowired
    private ArgsResolver argsResolver;
    @Autowired
    private BeanFactory beanFactory;
    @Autowired
    private ArgNameFormatter argNameFormatter;
    @Autowired
    private ParseCfgProps cfgProps;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private NebulaJdbcProperties properties;

    @Bean
    public Env getEnv() throws NoSuchFieldException, IllegalAccessException {
        return new Env(
                resourceLoader,
                nebulaPool(),
                textResolver,
                resultResolver,
                argsResolver,
                beanFactory,
                argNameFormatter,
                cfgProps,
                context,
                properties.getUsername(),
                properties.getPassword(),
                false,
                properties.getSpace()
        );
    }

    @Bean
    public NebulaPool nebulaPool() {
        NebulaPool pool = new NebulaPool();
        try {
            pool.init(properties.getHostAddresses(), properties.getPoolConfig());
        } catch (UnknownHostException e) {
            throw new RuntimeException("Can not connect to Nebula Graph");
        }
        return pool;
    }


    @Bean
    public MapperContext mapperContext(Env env ) {
        return env.getMapperContext();
    }


    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
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

    public NebulaJdbcProperties getProperties() {
        return properties;
    }

    public void setProperties(NebulaJdbcProperties properties) {
        this.properties = properties;
    }
}

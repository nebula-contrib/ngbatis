// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.config;

import org.springframework.context.annotation.Configuration;
import ye.weicheng.ngbatis.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;


/**
 * 环境配置类，用于存放框架的配置信息
 *
 * @author yeweicheng
 * <br>Now is history!
 */
@Configuration
public class EnvConfig {

    @Autowired
    private TextResolver textResolver;
    @Autowired
    private ResultResolver resultResolver;
    @Autowired
    private ArgsResolver argsResolver;
    @Autowired
    private ArgNameFormatter argNameFormatter;
    @Autowired
    private ParseCfgProps cfgProps;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private NebulaJdbcProperties properties;
    @Autowired(required = false)
    private PkGenerator pkGenerator;


    @Bean
    public Env getEnv() {
        return new Env(
                textResolver,
                resultResolver,
                argsResolver,
                argNameFormatter,
                cfgProps,
                context,
                properties.getUsername(),
                properties.getPassword(),
                false,
                properties.getSpace(),
                pkGenerator
        );
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

    public NebulaJdbcProperties getProperties() {
        return properties;
    }

    public void setProperties(NebulaJdbcProperties properties) {
        this.properties = properties;
    }
}

package org.nebula.contrib.ngbatis;

// Copyright (c) 2022 nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.alibaba.fastjson.parser.ParserConfig;
import com.vesoft.nebula.client.graph.net.Session;
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

    // 使用 fastjson 安全模式，规避任意代码执行风险
    static {
        ParserConfig.getGlobalInstance().setSafeMode(true);
    }

    public static ClassLoader classLoader;

    private Logger log = LoggerFactory.getLogger( Env.class );

//    private SessionFactory sessionFactory;
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


    public Env() {}

    private MapperContext mapperContext;

    public Env(
            TextResolver textResolver,
            ResultResolver resultResolver, ArgsResolver argsResolver,
            ArgNameFormatter argNameFormatter, ParseCfgProps cfgProps, ApplicationContext applicationContext,
            String username, String password, boolean reconnect, String space,
            PkGenerator pkGenerator
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
        log.debug( " Env constructor ");
    }

    // FIXME 使用连接池替换 THREAD_LOCAL 的实现方式
    static final ThreadLocal<Session> SESSION_CACHE = new ThreadLocal<>();

    public Session openSession() {
        try {
            return mapperContext.getNebulaPool().getSession(username, password, reconnect);
//            Session sessionCache = SESSION_CACHE.get();
//            if( sessionCache != null && sessionCache.ping() ) return sessionCache;
//            Session session = mapperContext.getNebulaPool().getSession(username, password, reconnect);
//            SESSION_CACHE.set( session );
//            log.info( "Nebula Graph sessionId: {}", session );
//            return session;
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

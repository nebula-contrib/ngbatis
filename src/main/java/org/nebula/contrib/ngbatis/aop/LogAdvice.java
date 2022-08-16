package org.nebula.contrib.ngbatis.aop;

// Copyright (c) 2022 nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.nebula.contrib.ngbatis.annotations.TimeLog;
import org.nebula.contrib.ngbatis.config.ParseCfgProps;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 方法执行时间切面
 *
 * @author yeweicheng
 * <br>Now is history!
 */
@Aspect
@Component
public class LogAdvice {

    private Logger logger = LoggerFactory.getLogger( LogAdvice.class );
    @Autowired
    private ParseCfgProps props;
    @Pointcut( value = "@annotation(org.nebula.contrib.ngbatis.annotations.TimeLog)")
    public void timeLog() {}

    @Around( "timeLog()")
    public Object msCount(ProceedingJoinPoint pjp ) throws Throwable{
        TimeLog annotation = ((MethodSignature) pjp.getSignature()).getMethod().getAnnotation(TimeLog.class);
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long end = System.currentTimeMillis();
        String name = annotation.name();
        if( props.getLogShowTypes().contains( name ) ) {
            logger.info( annotation.explain(), end - start );
        }
        return result;
    }


}

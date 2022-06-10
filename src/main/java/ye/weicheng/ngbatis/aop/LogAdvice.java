// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.aop;

import ye.weicheng.ngbatis.annotations.TimeLog;
import ye.weicheng.ngbatis.config.ParseCfgProps;
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
    @Pointcut( value = "@annotation(ye.weicheng.ngbatis.annotations.TimeLog)")
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

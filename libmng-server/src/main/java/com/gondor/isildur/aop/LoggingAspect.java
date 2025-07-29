// 创建切面类 LoggingAspect.java
package com.gondor.isildur.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // 记录Controller访问日志
    @Before("execution(* com.gondor.isildur.controller.*.*(..))")
    public void logControllerAccess(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        logger.info("访问控制器: {}.{}()", className, methodName);
    }

    // 记录Service方法执行时间
    @Around("execution(* com.gondor.isildur.service..*.*(..))")
    public Object logServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().getName();
        logger.info("服务方法 {}.{}() 执行耗时: {} ms",
                joinPoint.getTarget().getClass().getSimpleName(),
                methodName,
                endTime - startTime);

        return result;
    }
}
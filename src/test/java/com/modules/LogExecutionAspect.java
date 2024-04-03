package com.modules;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogExecutionAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("@annotation(LogExecution)")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        logger.info("S ------------------------------ " + className + " - " + methodName + " ------------------------------ S");

        // execute the decorated method, get the result
        Object result = joinPoint.proceed();

        logger.info("E ------------------------------ " + className + " - " + methodName + " ------------------------------ E");

        // return the result
        return result;
    }
}
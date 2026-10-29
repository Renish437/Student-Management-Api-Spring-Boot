package com.renish.backend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.renish.backend.service.impl..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint)
            throws Throwable {

        String method = joinPoint.getSignature().toShortString();

        long start = System.nanoTime();

        log.debug("START {} args={}",
                method, Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();

            long time = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - start
            );

            log.info("SUCCESS {} time={}ms", method, time);

            return result;

        } catch (Throwable e) {

            long time = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - start
            );

            log.error("FAILED {} time={}ms",
                    method, time, e);

            throw e;
        }
    }
}
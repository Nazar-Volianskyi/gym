package com.epam.gym.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class DaoStatementLoggingAspect {

    @Around("execution(* com.epam.gym.dao.*Impl.*(..))")
    public Object logStatement(ProceedingJoinPoint joinPoint) throws Throwable {
        String operation = joinPoint.getSignature().toShortString();
        String userId = CurrentUser.username();
        long startNanos = System.nanoTime();

        try {
            Object result = joinPoint.proceed();
            log.info("STATEMENT | operation={} | userId={} | result=SUCCESS | durationMs={}",
                    operation, userId, durationMs(startNanos));
            return result;
        } catch (Throwable ex) {
            log.info("STATEMENT | operation={} | userId={} | result=FAILED | durationMs={} | reason={}",
                    operation, userId, durationMs(startNanos), ex.getMessage());
            throw ex;
        }
    }

    private long durationMs(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }
}

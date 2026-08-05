package com.epam.gym.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class TransactionLoggingAspect {

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object logTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        String operation = joinPoint.getSignature().toShortString();
        String userId = CurrentUser.username();
        String transactionId = UUID.randomUUID().toString();
        MDC.put(LoggingConstants.TRANSACTION_ID_KEY, transactionId);
        long startNanos = System.nanoTime();

        log.info("TRANSACTION START | operation={} | userId={}", operation, userId);
        try {
            Object result = joinPoint.proceed();
            log.info("TRANSACTION END | operation={} | userId={} | result=SUCCESS | durationMs={}",
                    operation, userId, durationMs(startNanos));
            return result;
        } catch (Throwable ex) {
            log.info("TRANSACTION END | operation={} | userId={} | result=FAILED | durationMs={} | reason={}",
                    operation, userId, durationMs(startNanos), ex.getMessage());
            throw ex;
        } finally {
            MDC.remove(LoggingConstants.TRANSACTION_ID_KEY);
        }
    }

    private long durationMs(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }
}

package com.aioannou.library.logging;

import com.aioannou.library.data.BookRequest;
import com.aioannou.library.data.IsbnRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Logging for incoming requests
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.aioannou.library.controller.*.*(..))")
    public void logBefore(final JoinPoint joinPoint){
        final String parameters = getArgs(joinPoint.getArgs());
        LOGGER.info("Entering method: {} with arguments {}", joinPoint.getSignature().getName(), parameters);
    }

    @After("execution(* com.aioannou.library.controller.*.*(..))")
    public void logAfter(final JoinPoint joinPoint){
        LOGGER.info("Exiting method: {}", joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(* com.aioannou.library.controller.*.*(..))", returning = "entity")
    public void logOverload(final JoinPoint joinPoint, final Object entity) {
        if (entity instanceof ResponseEntity<?> response && response.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
            LOGGER.warn("Method: {} reported overload", joinPoint.getSignature().getName());
        }
    }

    /**
     * Get a list of incoming parameters
     * @return List of parameters
     */
    private String getArgs(final Object[] arguments) {
        final StringBuilder argumentsList = new StringBuilder();
        for (Object argument: arguments) {
            if (argument instanceof String value) {
                argumentsList.append(value).append(" ");
            }
            else if (argument instanceof BookRequest request) {
                argumentsList.append(request.isbn()).append(" ");
            }
            else if (argument instanceof IsbnRequest request) {
                argumentsList.append(request.isbn()).append(" ");
            }
        }
        return argumentsList.toString();
    }
}

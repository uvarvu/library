package com.aioannou.library.logging;

import com.aioannou.library.data.BookRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logging for incoming requests
 */
@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.aioannou.library.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint){
        String parameters = getArgs(joinPoint.getArgs());
        System.out.println(getDate() + " Entering method: " + joinPoint.getSignature().getName() + " with arguments " + parameters);
    }

    @After("execution(* com.aioannou.library.controller.*.*(..))")
    public void logAfter(JoinPoint joinPoint){
        System.out.println(getDate() + " Exiting method: " + joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(* com.aioannou.library.controller.*.*(..))", returning = "entity")
    public void logOverload(JoinPoint joinPoint, Object entity) {
        ResponseEntity response = (ResponseEntity) entity;
        if (response.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)){
            System.out.println(getDate() + " Method: " + joinPoint.getSignature().getName() + " reported overload");
        }
    }

    /**
     * Get a list of incoming parameters
     * @return List of parameters
     */
    private String getArgs(final Object[] arguments){
        StringBuilder argumentsList = new StringBuilder();
        for (Object argument: arguments){
            if (argument instanceof String){
                argumentsList.append((String) argument).append(" ");
            }
            else if(argument instanceof BookRequest){
                argumentsList.append(((BookRequest) argument).getIsbn()).append(" ");
            }
        }
        return argumentsList.toString();
    }

    /**
     * Get a formatted date for logging
     * @return formatted date
     */
    private String getDate(){
        return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }
}

package com.epam.rd.autocode.assessment.appliances.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingServices {

    /**
     * Pointcut: any method annotated with @Loggable
     */
    @Pointcut("@annotation(com.epam.rd.autocode.assessment.appliances.aspect.Loggable)")
    public void loggableMethods() {
    }

    /**
     * Pointcut: all methods in the service package
     */
    @Pointcut("execution(* com.epam.rd.autocode.assessment.appliances.service.impl..*(..))")
    public void serviceMethods() {
    }

    /**
     * Pointcut: all methods in the controller package
     */
    @Pointcut("execution(* com.epam.rd.autocode.assessment.appliances.controller..*(..))")
    public void controllerMethods() {
    }

    // ============ BEFORE ============

    /**
     * Logs before execution of methods annotated with @Loggable
     */
    @Before("loggableMethods()")
    public void logBeforeLoggable(JoinPoint joinPoint) {
        log.info("[@Loggable] Entering method: {}.{}() with args: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs())
        );
    }

    /**
     * Logs before execution of service methods
     */
    @Before("serviceMethods()")
    public void logBeforeService(JoinPoint joinPoint) {
        log.debug("[SERVICE] Calling: {}.{}()",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName()
        );
    }

    /**
     * Logs before execution of controller methods
     */
    @Before("controllerMethods()")
    public void logBeforeController(JoinPoint joinPoint) {
        log.debug("[CONTROLLER] Handling: {}.{}()",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName()
        );
    }

    // ============ AFTER RETURNING ============

    /**
     * Logs after successful execution of methods annotated with @Loggable
     */
    @AfterReturning(pointcut = "loggableMethods()", returning = "result")
    public void logAfterReturningLoggable(JoinPoint joinPoint, Object result) {
        log.info("[@Loggable] Method {}.{}() returned: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                result
        );
    }

    /**
     * Logs after successful execution of service methods
     */
    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturningService(JoinPoint joinPoint, Object result) {
        log.debug("[SERVICE] Method {}.{}() completed successfully",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName()
        );
    }

    // ============ AFTER THROWING ============

    /**
     * Logs exceptions thrown by service methods
     */
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "exception")
    public void logAfterThrowingService(JoinPoint joinPoint, Throwable exception) {
        log.error("[SERVICE] Exception in {}.{}(): {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                exception.getMessage(),
                exception
        );
    }

    /**
     * Logs exceptions thrown by controller methods
     */
    @AfterThrowing(pointcut = "controllerMethods()", throwing = "exception")
    public void logAfterThrowingController(JoinPoint joinPoint, Throwable exception) {
        log.error("[CONTROLLER] Exception in {}.{}(): {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                exception.getMessage()
        );
    }

    // ============ AROUND ============

    /**
     * Logs execution time of methods annotated with @Loggable
     */
    @Around("loggableMethods()")
    public Object logAroundLoggable(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("[@Loggable] START: {}.{}() with args: {}",
                className, methodName, Arrays.toString(args));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("[@Loggable] ERROR: {}.{}() failed after {} ms: {}",
                    className, methodName, duration, ex.getMessage());
            throw ex;
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("[@Loggable] END: {}.{}() completed in {} ms",
                className, methodName, duration);

        return result;
    }

    /**
     * Logs execution time of service methods
     */
    @Around("serviceMethods()")
    public Object logAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.debug("[SERVICE] START: {}.{}()", className, methodName);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.warn("[SERVICE] ERROR: {}.{}() failed after {} ms",
                    className, methodName, duration);
            throw ex;
        }

        long duration = System.currentTimeMillis() - startTime;
        log.debug("[SERVICE] END: {}.{}() completed in {} ms",
                className, methodName, duration);

        return result;
    }

    /**
     * Logs execution time of controller methods
     */
    @Around("controllerMethods()")
    public Object logAroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.debug("[CONTROLLER] START: {}.{}()", className, methodName);

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - startTime;
        log.debug("[CONTROLLER] END: {}.{}() completed in {} ms",
                className, methodName, duration);

        return result;
    }
}
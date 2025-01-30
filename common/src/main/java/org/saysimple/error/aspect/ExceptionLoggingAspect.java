package org.saysimple.error.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {

    @Around("execution(* org.saysimple..*Port.*(..))") // 서비스 레이어 대상
    public Object logAndHandleException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            log.error("[System Exception] {} - {}",
                joinPoint.getSignature().toShortString(), 
                ex.getMessage(), 
                ex
            );
        }
        return null;
    }
}
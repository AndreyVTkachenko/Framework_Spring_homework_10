package ru.gb.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* ru.gb.service..*(..))")
    public void serviceMethodsPointcut() {
    }

    @Before(value = "serviceMethodsPointcut()")
    public void beforeServiceMethod(JoinPoint jp) {
        String methodName = jp.getSignature().getName();
        Object[] args = jp.getArgs();
        StringBuilder argsInfo = new StringBuilder();

        for (Object arg : args) {
            if (argsInfo.length() > 0) {
                argsInfo.append(", ");
            }
            argsInfo.append(arg.getClass().getSimpleName()).append(" = ").append(arg);
        }

        log.info("Before -> {}#{}({})", jp.getTarget().getClass().getSimpleName(), methodName, argsInfo);
    }
}

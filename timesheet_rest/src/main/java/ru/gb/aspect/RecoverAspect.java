package ru.gb.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class RecoverAspect {

    @Around("@within(ru.gb.aspect.Recover)")
    public Object handleRecover(ProceedingJoinPoint pjp) {
        Method method = Arrays.stream(pjp.getTarget().getClass().getMethods())
                .filter(m -> m.getName().equals(pjp.getSignature().getName()))
                .findFirst()
                .orElse(null);

        if (method != null) {
            try {
                return pjp.proceed();
            } catch (Throwable ex) {
                log.error("Recovering {}#{} after Exception[{}, \"{}\"]",
                        pjp.getTarget().getClass().getSimpleName(),
                        method.getName(),
                        ex.getClass().getSimpleName(),
                        ex.getMessage());

                return getDefaultValue(method.getReturnType());
            }
        }

        try {
            return pjp.proceed();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    private Object getDefaultValue(Class<?> returnType) {
        if (returnType.isPrimitive()) {
            return switch (returnType.getName()) {
                case "boolean" -> false;
                case "byte" -> (byte) 0;
                case "short" -> (short) 0;
                case "int" -> 0;
                case "long" -> 0L;
                case "float" -> 0.0f;
                case "double" -> 0.0;
                case "char" -> '\u0000';
                default -> throw new IllegalArgumentException("Не является примитивным типом: " + returnType.getName());
            };
        }
        return null;
    }
}

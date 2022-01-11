package com.example.springlimiter;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Use AOP and annotation to decouple the business logic and traffic limiter.
 * So we only need to add the annotation on URL when needed.
 */
@Slf4j
@Aspect
@Component
public class LimitAop {
    private final Map<String, RateLimiter> limitMap = Maps.newConcurrentMap();

    @Around("@annotation(com.example.springlimiter.Limit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Limit annotation = method.getAnnotation(Limit.class);
        if (annotation != null) {
            // 1, Get key: different keys for different URLs and limits
            String key = annotation.key();
            // 2, Init rateLimiter:
            RateLimiter rateLimiter = null;

            // 3, If the key is not found from the buffer, create a token bucket:
            if (!limitMap.containsKey(key)) {
                double permits = annotation.permitsPerSecond();
                rateLimiter = RateLimiter.create(permits);
                limitMap.put(key, rateLimiter);
                log.info("New token bucket is created = {}, capacity = {}", key, permits);
            }
            rateLimiter = limitMap.get(key);

            // 4, Get the token:
            boolean acquired = rateLimiter.tryAcquire(annotation.timeout(), annotation.timeunit());

            // If not acquired, return a msg:
            if (!acquired) {
                log.debug("Token bucket = {}, failed to get token.", key);
                // Report to frontend:
                this.responseFail(annotation.msg());
                // If no frontend yet, just log in terminal:
                System.out.println(annotation.msg());
                return null;
            }
        }
        return joinPoint.proceed();

    }

    /**
     * Pass the message to front.
     * @param msg
     */
    private void responseFail(String msg) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //todo: Other things to implement:
//        ResultData<Object> resultData = ResultData.fail(ReturnCode.LIMIT_ERROR.getCode(), msg);
//        WebUtils.writeJson(response, resultData);
    }
}

package com.pri.tool.commonredis.lock.aspect;

import com.pri.tool.commonredis.lock.DistributedRedisLock;
import com.pri.tool.commonredis.lock.annotation.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author wang.song
 * @date 2020-12-09 16:01
 * @Desc
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class DistributedLockAspect {

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        return execute(joinPoint, distributedLock);
    }

    /**
     * 执行
     *
     * @param joinPoint
     * @param lock
     * @throws Throwable
     */
    private Object execute(ProceedingJoinPoint joinPoint, DistributedLock lock) throws Throwable {
        Object proceed = null;
        //执行
        try {
            String key = getKeyName(lock);
            boolean acquire = DistributedRedisLock.acquire(key, lock.expireTime(), lock.timeUnit());
            long start = System.currentTimeMillis();
            log.info("DistributedLockAspect.getLock:{}", acquire);
            proceed = joinPoint.proceed();
            DistributedRedisLock.release(key);
            log.info("DistributedLockAspect.release.time:{}", System.currentTimeMillis() - start);
        } catch (Throwable throwable) {
            log.error("DistributedLockAspect.error:{}", throwable);
        }
        return proceed;
    }

    /**
     * 获取key
     *
     * @param lock
     * @return
     */
    private String getKeyName(DistributedLock lock) {
        return lock.lockType().toString() + "_" + lock.key();
    }
}

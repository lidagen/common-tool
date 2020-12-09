package com.pri.tool.commonredis.lock.annotation;

import com.pri.tool.commonredis.lock.config.LockEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author wang.song
 * @date 2020-12-09 16:05
 * @Desc 分布式锁注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    /**
     * 锁前缀，业务划分
     *
     * @return
     */
    LockEnum lockType() default LockEnum.DEFAULT;

    /**
     * redisKey
     *
     * @return
     */
    String key() default "";

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 失效时间
     *
     * @return
     */
    int expireTime() default 6;

    /**
     * 等待时间
     *
     * @return
     */
    int waitTime() default 36;
}

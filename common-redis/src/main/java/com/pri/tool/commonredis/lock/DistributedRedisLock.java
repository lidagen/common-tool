package com.pri.tool.commonredis.lock;

import com.pri.tool.commonredis.lock.config.RedissonConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @author wang.song
 * @date 2020-12-09 16:13
 * @Desc 锁执行方法
 */
@Slf4j
public class DistributedRedisLock {
    /**
     * 从配置类中获取redisson对象
     * **/
    private static Redisson redisson = RedissonConfiguration.getRedisson();
    private static final String LOCK_TITLE = "redisLock_";

    /**
     * 加锁
     * @param lockName 锁名称
     * @param timeout 加锁时间
     * @return
     */
    public static boolean acquire(String lockName,int timeout,TimeUnit timeUnit){
        //声明key对象
        String key = LOCK_TITLE + lockName;
        //获取锁对象
        RLock mylock = redisson.getLock(key);
        //加锁，并且设置锁过期时间，防止死锁的产生
        try {
            return mylock.tryLock(timeout, timeUnit);
        } catch (InterruptedException e) {
            log.error("======lock,e:{}======" + Thread.currentThread().getName(),e);
            return Boolean.FALSE;
        }
    }

    /**
     * 锁释放
     * @param lockName
     */
    public static void release(String lockName){
        //必须是和加锁时的同一个key
        String key = LOCK_TITLE + lockName;
        //获取所对象
        RLock mylock = redisson.getLock(key);
        //释放锁（解锁）
        mylock.unlock();
        log.info("======unlock======"+Thread.currentThread().getName());
    }
}

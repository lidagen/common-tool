package com.pri.tool.commonredis.token.aspect;

import com.pri.tool.commonredis.bloom.RedisBloomUtil;
import com.pri.tool.commonredis.token.annotation.NoRepeat;
import com.pri.tool.commonredis.util.RedisUtil;
import com.pri.tool.commonreq.filter.bean.PublicParam;
import com.pri.tool.commonreq.filter.session.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author wang.song
 * @date 2020-12-09 18:31
 * @Desc
 */
@Aspect
@Component
@Order(2)
@Slf4j
public class TokenAspect {
    @Autowired
    private RedisBloomUtil redisTemplate;


    @Around("@annotation(noRepeat)")
    public Object around(ProceedingJoinPoint joinPoint, NoRepeat noRepeat) throws Throwable {
        return execute(joinPoint, noRepeat);
    }

    private Object execute(ProceedingJoinPoint joinPoint, NoRepeat noRepeat) throws Throwable {
        Object proceed = null;
        PublicParam publicParam = SessionUtil.get();
        //执行
        try {
            //保存redis
            if (redisTemplate.getRedisTemplate().hasKey(publicParam.getUserName())){
                //TODO 抛重复提交异常，给统一异常捕捉
                return null;
            }else {
                redisTemplate.getRedisTemplate().opsForValue().set(publicParam.getUserName(),publicParam.getToken(),2,TimeUnit.SECONDS);
            }
            log.info("TokenAspect.execute:{}");
             proceed = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("TokenAspect.error:{}", throwable);
        }finally {
           /* if (  redisTemplate.getRedisTemplate().hasKey(publicParam.getUserName())){
                redisTemplate.getRedisTemplate().delete(publicParam.getUserName());
            }*/
        }
        return proceed;

    }
}

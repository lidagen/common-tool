package com.pri.tool.commonredis.util;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author wang.song
 * @date 2020-12-09 18:39
 * @Desc
 */
@Slf4j
public class RedisUtil {
    public static RedisTemplate<Serializable, Object> redisTemplate;

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public static void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key,支持通配符
     *
     * @param pattern
     */
    public static void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * @param keys
     * @return java.util.Set<java.io.Serializable>
     * @describe: 匹配所有的key
     * @author: jiahong.xing/
     * @version: v1.0
     * @date 2017/09/21 下午 6:25
     */
    public static Set<Serializable> keys(final String keys) {
        Set<Serializable> keySet = redisTemplate.keys(keys);
        return keySet;
    }

    /**
     * @param keys
     * @return java.util.List<java.lang.Object>
     * @describe: 根据匹配规则得到数据，只适用于单实例，多实例慎用
     * @author: jiahong.xing/
     * @version: v1.0
     * @date 2017/09/21 下午 6:29
     */
    public static Map<Serializable, Object> getByKeys(final String keys) {
        Set<Serializable> set = keys(keys);
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }
        Map<Serializable, Object> map = new HashMap<Serializable, Object>(100);
        for (Serializable ser : set) {
            map.put(ser, get((String) ser));
        }
        return map;
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public static void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 清空数据，慎用
     */
    public synchronized static void flushall() {
        if (null != redisTemplate) {
            RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();

            connection.flushDb();
            connection.flushAll();

        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public static boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public static Object get(final String key) {
        Object result = null;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate
                    .opsForValue();

            result = operations.get(key);
        } catch (Exception e) {
            log.error("{}", e);
        }

        return result;
    }

    /**
     * @param key   键
     * @param value 值
     * @return boolean
     * @describe: 写入缓存
     * @author: jiahong.xing/
     * @version: v1.0
     * @date 2017/7/27 15:38
     */
    public static boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate
                    .opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            log.error("{}", e);
        }
        return result;
    }

    /**
     * @param key        键
     * @param value      值
     * @param expireTime 超时时间，秒
     * @return boolean
     * @describe: 写入缓存
     * @author: jiahong.xing/
     * @version: v1.0
     * @date 2017/7/27 15:38
     */
    public static boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate
                    .opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            log.error("{}", e);
        }
        return result;
    }

    /**
     * @param key        键
     * @param value      值
     * @param expireTime 超时时间，秒
     * @return boolean
     * @describe: 写入缓存, key为set名称（编号），value为该set下的值，set不允许重复
     * @version: v1.0
     * @date 2018/8/20/020 14:44
     */
    public static boolean addSet(final String key, final Object value, Long expireTime) {
        if (Objects.isNull(key) || null == value) {
            return false;
        }
        boolean result = false;
        try {
            SetOperations<Serializable, Object> setOperations = redisTemplate.opsForSet();
            Long longs = setOperations.add(key, value);
            //表示添加成功
            if (longs > 0) {
                redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
                result = true;
            }
        } catch (Exception e) {
            log.error("{}", e);
        }
        return result;
    }

    /**
     * @describe: 获取缓存, key为set名称（编号），或者
     * @author: jiahong.xing/
     * @version: v1.0
     * @date 2018/8/20/020 15:03
     */
    public static Object getSet(final String key) {
        Object result = null;
        try {
            SetOperations<Serializable, Object> setOperations = redisTemplate.opsForSet();
            result = setOperations.members(key);
        } catch (Exception e) {
            log.error("{}", e);
        }

        return result;
    }

    /**
     * 在redisList里面添加一个值，添加值在左边添加，弹出的时候可以在右边弹出(先进先出)，适用于秒杀场景
     *
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    public static boolean addList(final String key, final String value, Long expireTime) {

        if (Objects.isNull(key) || null == value) {
            return false;
        }
        boolean result = false;
        try {
            Long longs = redisTemplate.opsForList().rightPush(key, value);
            //表示添加成功
            if (longs > 0) {
                redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
                result = true;
            }
        } catch (Exception e) {
            log.error("{}", e);
        }
        return result;
    }

    /**
     * 在redisList里面弹出一个值，默认弹出的是右弹出，先进先出
     *
     * @param key
     * @return
     */
    public static Object rightPopList(final String key) {
        if (Objects.isNull(key)) {
            return null;
        }
        Object object = null;
        try {
            ListOperations<Serializable, Object> listOperations = redisTemplate.opsForList();
            object = listOperations.rightPop(key);
        } catch (Exception e) {
            log.error("{}", e);
        }
        return object;
    }

    /**
     * 在redisList获取长度
     *
     * @param key
     * @return
     */
    public static Long sizeList(final String key) {
        Long count = -1L;
        if (Objects.isNull(key)) {
            return count;
        }
        try {
            ListOperations<Serializable, Object> listOperations = redisTemplate.opsForList();
            count = listOperations.size(key);
        } catch (Exception e) {
            log.error("{}", e);
        }
        return count;
    }


    /**
     * 在redisList获取列表
     *
     * @param key
     * @return
     */
    public static List getList(final String key) {
        if (Objects.isNull(key)) {
            return null;
        }
        List list = new ArrayList();
        try {
            ListOperations<Serializable, Object> listOperations = redisTemplate.opsForList();
            list = listOperations.range(key, 0, sizeList(key));
        } catch (Exception e) {
            log.error("{}", e);
        }
        return list;
    }

    /**
     * 移除set里面的一个值
     *
     * @param key
     * @param value
     */
    public static void removeSet(final String key, final String value) {
        if (exists(key)) {
            try {
                SetOperations<Serializable, Object> setOperations = redisTemplate.opsForSet();
                setOperations.remove(key, value);
            } catch (Exception e) {
                log.error("{}", e);
            }
        }
    }


    /**
     * 获取剩余时间
     *
     * @param key
     * @return
     */
    public static Long getTime(String key) {
        if (Objects.isNull(key)) {
            return (long) -1;
        }
        Long time = redisTemplate.getExpire(key);
        return time;
    }



    /**
     * 根据hash key取值
     *
     * @param hashName
     * @param key
     * @return
     */
    public static Object hget(String hashName, String key) {
        Object object = null;
        try {
            object = redisTemplate.opsForHash().get(hashName, key);
        } catch (Exception e) {
            log.error("{}", e);
        }
        return object;
    }


    public static Long hdel(String hashName, String key) {
        Long count = 0L;
        try {
            count = redisTemplate.opsForHash().delete(hashName, key);
        } catch (Exception e) {
            log.error("{}", e);
        }
        return count;
    }

    public static Map<Object, Object> hgetAll(String key) {
        Map<Object, Object> entries = Maps.newHashMap();
        try {
            HashOperations<Serializable, Object, Object> hashOperations = redisTemplate.opsForHash();

            entries = hashOperations.entries(key);
        } catch (Exception e) {
            log.error("{}", e);
        }
        return entries;
    }

    public static void hset(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            log.error("{}", e);
        }
    }

    /**
     * 判断key是否存在
     *
     * @param hashName
     * @param key
     * @return
     */
    public static Boolean hexists(String hashName, String key) {
        Boolean flag = false;
        try {
            flag = redisTemplate.opsForHash().hasKey(hashName, key);
        } catch (Exception e) {
            log.error("{}", e);
        }
        return flag;
    }
}

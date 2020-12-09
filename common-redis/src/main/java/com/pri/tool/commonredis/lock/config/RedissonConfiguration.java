package com.pri.tool.commonredis.lock.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author wang.song
 * @date 2020-12-09 11:50
 * @Desc redisson 配置
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MyRedisProperties.class)
public class RedissonConfiguration {
    /**
     * 构建前缀
     */
    private static final String SENTINEL_PREFIX = "redis://";

    private static Config config = new Config();

    private static  Redisson redisson;


    @Bean
    @ConditionalOnMissingBean(RedissonClient.class)
    MyRedisProperties getProperties(MyRedisProperties myRedisProperties) {
        createRedisson(myRedisProperties);
        return myRedisProperties;
    }


    private void createRedisson(MyRedisProperties myRedisProperties) {
        Integer mode = myRedisProperties.getMode();
        setConfig(mode, myRedisProperties);
        redisson=(Redisson) Redisson.create(config);
    }

    public static Redisson getRedisson(){
        return redisson;
    }


    /**
     * 根据配置文件获取redis模式
     *
     * @param type
     * @param myRedisProperties
     */
    private void setConfig(Integer type, MyRedisProperties myRedisProperties) {
        switch (type) {
            case 1:
                //单机redis
                config.useSingleServer().setAddress(SENTINEL_PREFIX + myRedisProperties.getHostPort()).setPassword(myRedisProperties.getPassword());
                break;
            case 2:
                //哨兵
                String[] split = myRedisProperties.getSentinelnodes().split(",");
                List<String> arrayList = Lists.newArrayList();
                for (String sp : split) {
                    arrayList.add(SENTINEL_PREFIX + sp);
                }
                String[] strings = arrayList.toArray(new String[arrayList.size()]);
                config.useSentinelServers().addSentinelAddress(strings)
                        .setMasterName(myRedisProperties.getSentinelName()).setPassword(myRedisProperties.getPassword()).setDatabase(0);
                break;
            case 3:
                //TODO 集群
                config.useClusterServers().addNodeAddress(
                        "redis://172.29.3.245:6375", "redis://172.29.3.245:6376", "redis://172.29.3.245:6377",
                        "redis://172.29.3.245:6378", "redis://172.29.3.245:6379", "redis://172.29.3.245:6380")
                        .setPassword("a123456").setScanInterval(5000);
                break;
            default:
                //默认单机
                config.useSingleServer().setAddress("redis://172.0.0.1:6379");
        }
    }

}

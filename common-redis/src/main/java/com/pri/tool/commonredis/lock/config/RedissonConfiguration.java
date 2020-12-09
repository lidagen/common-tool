package com.example.bloom.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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


    @Bean
    @ConditionalOnMissingBean(RedissonClient.class)
    MyRedisProperties getProperties(MyRedisProperties myRedisProperties) {
        createRedisson(myRedisProperties);
        return myRedisProperties;
    }


    private Redisson createRedisson(MyRedisProperties myRedisProperties) {
        Integer mode = myRedisProperties.getMode();
        setConfig(mode, myRedisProperties);
        return redisson = (Redisson) Redisson.create(config);
    }

    private static final String sentinelPrx = "redis://";

    private static Config config = new Config();
    /**
     * 声明redisso对象
     **/
    private static Redisson redisson = null;


    /**
     * 获取redisson对象的方法
     **/
    public static Redisson getRedisson() {
        return redisson;
    }


    private void setConfig(Integer type, MyRedisProperties myRedisProperties) {
        switch (type) {
            case 1:
                String ip = sentinelPrx + myRedisProperties.getHost() + ":" + myRedisProperties.getPort();
                config.useSingleServer().setAddress(ip);
                break;
            case 2:
                String[] split = myRedisProperties.getSentinelnodes().split(",");
                List<String> arrayList = Lists.newArrayList();
                for (String sp : split) {
                    arrayList.add(sentinelPrx + sp);
                }
                String[] strings = arrayList.toArray(new String[arrayList.size()]);
                config.useSentinelServers().addSentinelAddress(strings)
                        .setMasterName(myRedisProperties.getSentinelName()).setDatabase(0);
                break;
            case 3:
                config.useClusterServers().addNodeAddress(
                        "redis://172.29.3.245:6375", "redis://172.29.3.245:6376", "redis://172.29.3.245:6377",
                        "redis://172.29.3.245:6378", "redis://172.29.3.245:6379", "redis://172.29.3.245:6380")
                        .setPassword("a123456").setScanInterval(5000);
                break;
        }
    }

}

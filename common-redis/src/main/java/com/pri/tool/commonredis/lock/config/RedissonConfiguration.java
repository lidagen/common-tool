package com.pri.tool.commonredis.lock.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wang.song
 * @date 2020-12-09 11:50
 * @Desc redisson 配置
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedissonConfiguration {
    /**
     * 构建前缀
     */
    private static final String REDIS_PREFIX = "redis://";


    private static Redisson redisson;

    /**
     * 单机模式 redisson 客户端
     */
    @Bean
    @ConditionalOnMissingBean(RedissonClient.class)
    RedissonClient redissonClient(RedisProperties redisProperties) {
        return createRedissonClient(redisProperties);
    }

    private RedissonClient createRedissonClient(RedisProperties properties) {
        Config config = new Config();
        if (properties.getSentinel() != null && properties.getSentinel().getNodes() != null &&
                Objects.nonNull(properties.getSentinel().getMaster())) {
            List<String> nodesList = properties.getSentinel().getNodes();
            List<String> collect = nodesList.stream().map(vo -> REDIS_PREFIX+vo).collect(Collectors.toList());

            config.useSentinelServers().setMasterName(properties.getSentinel().getMaster())
                    .addSentinelAddress(collect.toArray(new String[0])).setPassword(properties.getPassword());
        } else if (properties.getCluster() != null && properties.getCluster().getNodes() != null) {
            List<String> nodesList = properties.getCluster().getNodes();
            List<String> collect = nodesList.stream().map(vo -> REDIS_PREFIX+vo).collect(Collectors.toList());
            config.useClusterServers().addNodeAddress(collect.toArray(new String[0])).setPassword(properties.getPassword());
        } else {
            config.useSingleServer().setAddress(REDIS_PREFIX + properties.getHost() + ":" + properties.getPort()).setPassword(properties.getPassword());
        }
        redisson = (Redisson) Redisson.create(config);
        return Redisson.create(config);
    }

    public static Redisson getRedisson() {
        return redisson;
    }
}

package com.example.bloom.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wang.song
 * @date 2020-12-09 14:54
 * @Desc  获取properties中 spring.private.redis前缀的值
 */
@ConfigurationProperties(prefix = "spring.private.redis")
@Component
@Data
public class MyRedisProperties {

    private Integer mode;

    private String host;

    private String port;

    private String sentinelName;

    private String sentinelnodes;
}

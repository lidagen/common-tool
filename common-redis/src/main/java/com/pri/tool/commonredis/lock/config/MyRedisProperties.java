package com.pri.tool.commonredis.lock.config;

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
    /**
     * 模式
     */
    private Integer mode;
    /**
     * 密码
     */
    private String password;
    /**
     * 单机HOST:PORT
     */
    private String hostPort;
    /**
     * 单机port
     */
    private String port;
    /**
     * 哨兵名称
     */
    private String sentinelName;
    /**
     * 哨兵节点
     */
    private String sentinelnodes;
}

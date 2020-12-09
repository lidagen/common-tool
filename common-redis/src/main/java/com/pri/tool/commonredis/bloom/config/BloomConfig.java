package com.pri.tool.commonredis.bloom;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wang.song
 * @date 2020-12-09 10:05
 * @Desc 布隆过滤器初始化
 */
@Configuration
public class BloomConfig {
    @Bean
    public BloomFilterHelper<String> initBloomFilterHelper() {
        return new BloomFilterHelper<>((Funnel<String>) (from, into) -> into.putString(from, Charsets.UTF_8).putString(from, Charsets.UTF_8), 1000000, 0.01);
    }

}

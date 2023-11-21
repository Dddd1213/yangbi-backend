package com.example.yangbibackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *   星火大模型AI接口配置
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "xfun.open")
public class XfunConfig {
    private String appid;
    private String apiSecret;
    private String hostUrl;
    private String apiKey;
}


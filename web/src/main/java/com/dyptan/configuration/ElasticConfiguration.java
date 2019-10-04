package com.dyptan.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticConfiguration {
        private String host;
        private String index;
        private int port;
        private String type;
}

package com.dyptan.connector;

import com.dyptan.configuration.ElasticConfiguration;
import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
@Component
public class SearchConnector {
    private String host;
    private RestHighLevelClient client;
    @Autowired
    private ElasticConfiguration config;

    @PostConstruct
    public void initClient() {
        this.host = config.getHost();
        this.client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, config.getPort(), "http")));
    }
}

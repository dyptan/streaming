package com.dyptan.connector;

import com.dyptan.configuration.ElasticConfiguration;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SearchConnector {
    @Autowired
    private ElasticConfiguration config;
    private String host;
    private RestHighLevelClient client;

    public SearchConnector() {
    }

    @PostConstruct
    public void initClient() {
        this.host = config.getHost();
        this.client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, config.getPort(), "http")));
    }

    public ElasticConfiguration getConfig() {
        return this.config;
    }

    public String getHost() {
        return this.host;
    }

    public RestHighLevelClient getClient() {
        return this.client;
    }

    public void setConfig(ElasticConfiguration config) {
        this.config = config;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setClient(RestHighLevelClient client) {
        this.client = client;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof SearchConnector)) return false;
        final SearchConnector other = (SearchConnector) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$config = this.getConfig();
        final Object other$config = other.getConfig();
        if (this$config == null ? other$config != null : !this$config.equals(other$config)) return false;
        final Object this$host = this.getHost();
        final Object other$host = other.getHost();
        if (this$host == null ? other$host != null : !this$host.equals(other$host)) return false;
        final Object this$client = this.getClient();
        final Object other$client = other.getClient();
        if (this$client == null ? other$client != null : !this$client.equals(other$client)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SearchConnector;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $config = this.getConfig();
        result = result * PRIME + ($config == null ? 43 : $config.hashCode());
        final Object $host = this.getHost();
        result = result * PRIME + ($host == null ? 43 : $host.hashCode());
        final Object $client = this.getClient();
        result = result * PRIME + ($client == null ? 43 : $client.hashCode());
        return result;
    }

    public String toString() {
        return "SearchConnector(config=" + this.getConfig() + ", host=" + this.getHost() + ", client=" + this.getClient() + ")";
    }
}

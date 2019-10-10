package com.dyptan.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@EnableConfigurationProperties
@ConfigurationProperties
public class ElasticConfiguration {
        @Value(value="${elasticsearch.type}")
        private String type;
        @Value(value="${elasticsearch.index}")
        private String index;
        @Value(value="${elasticsearch.service.port}")
        private Integer port;
        @Value(value="${elasticsearch.service.host}")
        private String host;


        public ElasticConfiguration() {
        }

        public String getHost() {
                return this.host;
        }

        public String getIndex() {
                return this.index;
        }

        public Integer getPort() {
                return this.port;
        }

        public String getType() {
                return this.type;
        }

        public void setHost(String host) {
                this.host=host;
        }

        public void setIndex(String index) {
                this.index = index;
        }

        public void setPort(int port) {
                this.port=port;
        }

        public void setType(String type) {
                this.type = type;
        }

        public boolean equals(final Object o) {
                if (o == this) return true;
                if (!(o instanceof ElasticConfiguration)) return false;
                final ElasticConfiguration other = (ElasticConfiguration) o;
                if (!other.canEqual((Object) this)) return false;
                final Object this$host = this.getHost();
                final Object other$host = other.getHost();
                if (this$host == null ? other$host != null : !this$host.equals(other$host)) return false;
                final Object this$index = this.getIndex();
                final Object other$index = other.getIndex();
                if (this$index == null ? other$index != null : !this$index.equals(other$index)) return false;
                if (this.getPort() != other.getPort()) return false;
                final Object this$type = this.getType();
                final Object other$type = other.getType();
                if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
                return true;
        }

        protected boolean canEqual(final Object other) {
                return other instanceof ElasticConfiguration;
        }

        public int hashCode() {
                final int PRIME = 59;
                int result = 1;
                final Object $host = this.getHost();
                result = result * PRIME + ($host == null ? 43 : $host.hashCode());
                final Object $index = this.getIndex();
                result = result * PRIME + ($index == null ? 43 : $index.hashCode());
                result = result * PRIME + this.getPort();
                final Object $type = this.getType();
                result = result * PRIME + ($type == null ? 43 : $type.hashCode());
                return result;
        }

        public String toString() {
                return "ElasticConfiguration(host=" + this.getHost() + ", index=" + this.getIndex() + ", port=" + this.getPort() + ", type=" + this.getType() + ")";
        }
}

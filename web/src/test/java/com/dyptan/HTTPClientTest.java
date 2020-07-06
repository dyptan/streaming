package com.dyptan;

import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.junit.Assert;

public class HTTPClientTest {
    @Test
    public void getResponse() {
        WebClient webclient = WebClient.create("http://localhost:8088/api");
        String response = webclient.post().uri("/trainer?limit=1&iterations=2&path=localhost")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        Assert.assertEquals ( "[{\"path\": \"localhost\",\n\"limit\": \"2\",\n\"iterations\": \"1\"}]", response);
    }
}

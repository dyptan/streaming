package com.dyptan;

import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPClientTest {
    @Test
    public void getResponse() {
        WebClient webclient = WebClient.create("http://localhost:8088/api");
        String response = webclient.post().uri("/trainer?limit=1&iterations=2&path=localhost")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        assertEquals ( "[{\"path\": \"localhost\",\n\"limit\": \"2\",\n\"iterations\": \"1\"}]", response);
    }
}

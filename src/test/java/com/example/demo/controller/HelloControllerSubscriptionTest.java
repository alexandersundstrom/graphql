package com.example.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.WebSocketGraphQlTester;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.net.URI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloControllerSubscriptionTest {

    @Value("http://localhost:${local.server.port}${spring.graphql.websocket.path}")
    private String baseUrl;

    GraphQlTester graphQlTester;

    @BeforeEach
    void setup() {
        URI url = URI.create(baseUrl);
        this.graphQlTester = WebSocketGraphQlTester.builder(url, new ReactorNettyWebSocketClient())
                .build();
    }

    @Test
    void helloSubscription() {
        Flux<String> hello = graphQlTester
                .document("subscription mySubscription { helloReactive }")
                .executeSubscription()
                .toFlux("helloReactive", String.class);

        StepVerifier.create(hello)
                .expectNext("Hello 0")
                .expectNext("Hello 1")
                .expectNext("Hello 2")
                .verifyComplete();
    }
}

package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class AuthE2ETest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void shouldRejectUnauthenticated() {
        String document = "query orders { myOrders { id }} ";
        Map<String, String> body = Map.of("query", document);
        webTestClient
                .mutateWith((builder, httpHandlerBuilder, connector) -> builder.baseUrl("/graphql"))
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.FOUND);
    }
}

package com.example.demo.controller;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Controller
public class HelloController {

    @QueryMapping
    public String hello() {
        return "Hello World";
    }

    @SubscriptionMapping
    Flux<String> helloReactive() {
        Flux<Integer> interval = Flux.fromIterable(List.of(0, 1, 2))
                .delayElements(Duration.ofSeconds(1));
        return interval.map(integer -> "Hello " + integer);
    }
}

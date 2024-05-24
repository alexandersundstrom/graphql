package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureHttpGraphQlTester  //Does not go through HTTP transport layer
public class MockedApplicationTest {

    @Autowired
    HttpGraphQlTester graphQlTester;

    @Test
    void usingTester() {
        graphQlTester
                .documentName("greeting") //Read from file in test resources
                // .document("query greeting { hello }") //Rent query
                .execute()
                .errors()
                .verify() //Ensure there are no GraphQL errors
                .path("hello")
                .entity(String.class)
                .isEqualTo("Hello World");
    }
}

package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(WebGraphQlTest.WebInterCeptor.class)
public class WebGraphQlTest {

    @Autowired
    WebGraphQlHandler webGraphQlHandler;

    @Component
    static class WebInterCeptor implements WebGraphQlInterceptor {

        @Override
        public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
            UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(
                    "Luna", "password",
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authenticated);
            return chain
                    .next(request)
                    .contextWrite(ReactiveSecurityContextHolder
                            .withSecurityContext(Mono.just(context)
                            ));
        }
    }

    @Test
    void testCorrectOrdersAreReturned() {
        WebGraphQlTester webGraphQlTester = WebGraphQlTester.create(webGraphQlHandler);
        String document = "query orders { myOrders { id }}";
        webGraphQlTester
                .document(document)
                .execute()
                .errors()
                .verify()
                .path("myOrders[*].id")
                .entityList(String.class)
                .isEqualTo(List.of("1", "3", "5", "6"));
    }

    @Test
    void testMutationForbidden() {
        WebGraphQlTester webGraphQlTester = WebGraphQlTester.create(webGraphQlHandler);
        String document = """
                mutation delete($id: ID) {
                    deleteOrder(input: { orderId: $id}) { 
                    success 
                    }
                }
                """;

        webGraphQlTester
                .document(document)
                .variable("id", "1")
                .execute()
                .errors()
                .expect(responseError -> responseError.getMessage().equals("Forbidden") && responseError.getPath().equals("deleteOrder"))
                .verify();
    }
}

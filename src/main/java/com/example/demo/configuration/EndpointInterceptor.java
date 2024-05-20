package com.example.demo.configuration;

import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class EndpointInterceptor implements WebGraphQlInterceptor {
    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {

        boolean hasAccess = request.getHeaders().containsKey("requestId");
        if (hasAccess) {
            request.configureExecutionInput((executionInput, builder) -> {
                executionInput
                        .getGraphQLContext()
                        .put("requestId", true); //Add to GraphQLContext
                return executionInput;
            });
        }
        request.configureExecutionInput((executionInput, builder) -> {
            executionInput
                    .getGraphQLContext()
                    .put("accessKey", "granted"); //Add to GraphQLContext
            return executionInput;
        });

        return chain.next(request)
               .map(response -> {
                   response.getResponseHeaders().add("special-header", "true"); //Adds header to response
                   return response;
               });
    }
}

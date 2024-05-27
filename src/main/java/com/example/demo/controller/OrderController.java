package com.example.demo.controller;

import com.example.demo.model.DeleteOrderInput;
import com.example.demo.model.DeleteOrderPayload;
import com.example.demo.model.Order;
import com.example.demo.service.OrderService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @QueryMapping
    //Principal holds user information, not used, but there as a reminder
    Mono<List<Order>> myOrders(Principal principal) {
        return orderService.getOrdersByOwner();
    }

    @MutationMapping
    public Mono<DeleteOrderPayload> deleteOrder(@Argument DeleteOrderInput input, Principal principal) {
        Mono<Boolean> booleanMono = orderService.deleteOrder(input.orderId());
        return booleanMono.map(DeleteOrderPayload::new);
    }
}

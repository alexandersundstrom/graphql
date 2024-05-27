package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity //hasRole
public class ApplicationSecurityConfig {

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
        http
                .formLogin()
                .authenticationSuccessHandler(
                        new RedirectServerAuthenticationSuccessHandler("/graphiql"));

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> {
                    exchanges.anyExchange().authenticated();
                })
                .build();
    }

    @Bean
    @SuppressWarnings("deprecation")
    MapReactiveUserDetailsService userDetailsService() {
        User.UserBuilder userBuilder = User.withDefaultPasswordEncoder();
        UserDetails luna = userBuilder
                .username("Luna")
                .password("password")
                .roles("USER")
                .build();

        UserDetails andi = userBuilder
                .username("Andi")
                .password("password")
                .roles("USER", "ADMIN")
                .build();

        return new MapReactiveUserDetailsService(luna, andi);
    }
}

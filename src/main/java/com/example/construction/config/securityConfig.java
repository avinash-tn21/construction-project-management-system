package com.example.construction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class securityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                        "/api/**",
                                        "/dashboard",
                                        "/projects",
                                        "/workforce",
                                        "/attendance",
                                        "/sites",
                                        "/style.css"
                                ).permitAll()

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
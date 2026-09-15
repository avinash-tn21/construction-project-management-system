package com.example.construction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class securityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // Login page
                        .requestMatchers(
                                "/login",
                                "/style.css",
                                "/error"
                        ).permitAll()

                        // Role-based areas - we will build these next
                        .requestMatchers("/client/**").hasRole("CLIENT")
                        .requestMatchers("/contractor/**").hasRole("CONTRACTOR")

                        // Existing pages temporarily accessible after login
                        .requestMatchers(
                                "/dashboard",
                                "/projects",
                                "/workforce",
                                "/attendance",
                                "/sites",
                                "/inventory",
                                "/equipment",
                                "/daily-logs",
                                "/api/**"
                        ).authenticated()

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/post-login", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }
}
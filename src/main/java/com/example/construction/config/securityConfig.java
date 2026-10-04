package com.example.construction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

                        // Client and contractor areas
                        .requestMatchers("/client/**").hasRole("CLIENT")
                        .requestMatchers("/contractor/**").hasRole("CONTRACTOR")

                        // Management pages
                        .requestMatchers("/dashboard", "/projects", "/clients",
                                "/contractors").hasRole("PROJECT_MANAGER")
                        .requestMatchers("/workforce", "/attendance")
                        .hasAnyRole("PROJECT_MANAGER", "SITE_SUPERVISOR")
                        .requestMatchers("/sites", "/daily-logs")
                        .hasAnyRole("PROJECT_MANAGER", "SITE_SUPERVISOR")
                        .requestMatchers("/inventory").hasRole("INVENTORY_MANAGER")
                        .requestMatchers("/equipment").hasRole("EQUIPMENT_MANAGER")
                        .requestMatchers("/notifications").authenticated()

                        // Client read-only project information. Ownership is
                        // checked in the relevant controllers/services.
                        .requestMatchers(HttpMethod.GET, "/api/projects/**",
                                "/api/sites/**", "/api/tasks/**",
                                "/api/milestones/**", "/api/daily-site-logs/**")
                        .hasAnyRole("CLIENT", "PROJECT_MANAGER", "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER", "EQUIPMENT_MANAGER")

                        // Project-management APIs
                        .requestMatchers("/api/projects/**", "/api/clients/**",
                                "/api/contractors/**", "/api/tasks/**",
                                "/api/milestones/**")
                        .hasRole("PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/sites/**")
                        .hasAnyRole("PROJECT_MANAGER", "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER", "EQUIPMENT_MANAGER")
                        .requestMatchers("/api/sites/**")
                        .hasAnyRole("PROJECT_MANAGER", "SITE_SUPERVISOR")
                        .requestMatchers("/api/workers/**", "/api/attendance/**")
                        .hasAnyRole("PROJECT_MANAGER", "SITE_SUPERVISOR")
                        .requestMatchers("/api/daily-site-logs/**")
                        .hasAnyRole("PROJECT_MANAGER", "SITE_SUPERVISOR")

                        // Inventory and equipment APIs
                        .requestMatchers("/api/inventory/**", "/api/material-requests/**")
                        .hasRole("INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/equipment/**",
                                "/api/equipment-allocations/**")
                        .hasAnyRole("PROJECT_MANAGER", "EQUIPMENT_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/equipment-allocations/**")
                        .hasAnyRole("PROJECT_MANAGER", "EQUIPMENT_MANAGER")
                        .requestMatchers("/api/equipment/**", "/api/equipment-allocations/**")
                        .hasRole("EQUIPMENT_MANAGER")

                        // User and notification data are always scoped to the
                        // authenticated user by their controllers.
                        .requestMatchers("/api/users/**", "/api/notifications/**")
                        .authenticated()

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

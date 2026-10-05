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
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // =========================
                // CSRF
                // =========================

                .csrf(csrf -> csrf.disable())


                // =========================
                // AUTHORIZATION
                // =========================

                .authorizeHttpRequests(auth -> auth


                        // =========================
                        // PUBLIC
                        // =========================

                        .requestMatchers(
                                "/login",
                                "/style.css",
                                "/error"
                        ).permitAll()


                        // =========================
                        // CLIENT
                        // =========================

                        .requestMatchers("/client/**")
                        .hasRole("CLIENT")


                        // =========================
                        // MANAGEMENT PAGES
                        // =========================

                        .requestMatchers(
                                "/dashboard",
                                "/projects",
                                "/clients",
                                "/contractors",
                                "/workforce",
                                "/attendance",
                                "/sites",
                                "/daily-logs",
                                "/inventory",
                                "/equipment",
                                "/notifications"
                        )
                        .hasAnyRole(
                                "PROJECT_MANAGER",
                                "CONTRACTOR",
                                "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER",
                                "EQUIPMENT_MANAGER",
                                "EMPLOYEE_MANAGER",
                                "CLIENT_MANAGER"
                        )


                        // =========================
                        // PROJECT API
                        // =========================

                        .requestMatchers("/api/projects/**")
                        .hasAnyRole(
                                "PROJECT_MANAGER",
                                "CONTRACTOR",
                                "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER",
                                "EQUIPMENT_MANAGER",
                                "EMPLOYEE_MANAGER",
                                "CLIENT_MANAGER"
                        )


                        // =========================
                        // CLIENT API
                        // =========================

                        .requestMatchers("/api/clients/**")
                        .hasAnyRole(
                                "PROJECT_MANAGER",
                                "CONTRACTOR",
                                "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER",
                                "EQUIPMENT_MANAGER",
                                "EMPLOYEE_MANAGER",
                                "CLIENT_MANAGER"
                        )


                        // =========================
                        // CONTRACTOR API
                        // =========================

                        .requestMatchers("/api/contractors/**")
                        .hasAnyRole(
                                "PROJECT_MANAGER",
                                "CONTRACTOR",
                                "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER",
                                "EQUIPMENT_MANAGER",
                                "EMPLOYEE_MANAGER",
                                "CLIENT_MANAGER"
                        )


                        // =========================
                        // TASK API
                        // =========================

                        .requestMatchers("/api/tasks/**")
                        .hasAnyRole(
                                "PROJECT_MANAGER",
                                "CONTRACTOR",
                                "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER",
                                "EQUIPMENT_MANAGER",
                                "EMPLOYEE_MANAGER",
                                "CLIENT_MANAGER"
                        )


                        // =========================
                        // MILESTONE API
                        // =========================

                        .requestMatchers("/api/milestones/**")
                        .hasAnyRole(
                                "PROJECT_MANAGER",
                                "CONTRACTOR",
                                "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER",
                                "EQUIPMENT_MANAGER",
                                "EMPLOYEE_MANAGER",
                                "CLIENT_MANAGER"
                        )


                        // =========================
                        // SITE API
                        // =========================

                        .requestMatchers("/api/sites/**")
                        .hasAnyRole(
                                "PROJECT_MANAGER",
                                "CONTRACTOR",
                                "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER",
                                "EQUIPMENT_MANAGER",
                                "EMPLOYEE_MANAGER",
                                "CLIENT_MANAGER"
                        )


                        // =========================
                        // WORKFORCE API
                        // =========================

                        .requestMatchers("/api/workers/**")
                        .hasAnyRole(
                                "PROJECT_MANAGER",
                                "CONTRACTOR",
                                "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER",
                                "EQUIPMENT_MANAGER",
                                "EMPLOYEE_MANAGER",
                                "CLIENT_MANAGER"
                        )


                        // =========================
                        // ATTENDANCE API
                        // =========================

                        .requestMatchers("/api/attendance/**")
                        .hasAnyRole(
                                "PROJECT_MANAGER",
                                "CONTRACTOR",
                                "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER",
                                "EQUIPMENT_MANAGER",
                                "EMPLOYEE_MANAGER",
                                "CLIENT_MANAGER"
                        )


                        // =========================
                        // DAILY LOG API
                        // =========================

                        .requestMatchers("/api/daily-site-logs/**")
                        .hasAnyRole(
                                "PROJECT_MANAGER",
                                "CONTRACTOR",
                                "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER",
                                "EQUIPMENT_MANAGER",
                                "EMPLOYEE_MANAGER",
                                "CLIENT_MANAGER"
                        )


                        // =========================
                        // INVENTORY API
                        // =========================

                        .requestMatchers("/api/inventory/**")
                        .hasAnyRole(
                                "PROJECT_MANAGER",
                                "CONTRACTOR",
                                "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER",
                                "EQUIPMENT_MANAGER",
                                "EMPLOYEE_MANAGER",
                                "CLIENT_MANAGER"
                        )


                        // =========================
                        // MATERIAL REQUEST API
                        // =========================

                        .requestMatchers("/api/material-requests/**")
                        .hasAnyRole(
                                "PROJECT_MANAGER",
                                "CONTRACTOR",
                                "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER",
                                "EQUIPMENT_MANAGER",
                                "EMPLOYEE_MANAGER",
                                "CLIENT_MANAGER"
                        )


                        // =========================
                        // EQUIPMENT API
                        // =========================

                        .requestMatchers("/api/equipment/**")
                        .hasAnyRole(
                                "PROJECT_MANAGER",
                                "CONTRACTOR",
                                "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER",
                                "EQUIPMENT_MANAGER",
                                "EMPLOYEE_MANAGER",
                                "CLIENT_MANAGER"
                        )


                        // =========================
                        // EQUIPMENT ALLOCATION API
                        // =========================

                        .requestMatchers("/api/equipment-allocations/**")
                        .hasAnyRole(
                                "PROJECT_MANAGER",
                                "CONTRACTOR",
                                "SITE_SUPERVISOR",
                                "INVENTORY_MANAGER",
                                "EQUIPMENT_MANAGER",
                                "EMPLOYEE_MANAGER",
                                "CLIENT_MANAGER"
                        )


                        // =========================
                        // USERS
                        // =========================

                        .requestMatchers("/api/users/**")
                        .authenticated()


                        // =========================
                        // NOTIFICATIONS
                        // =========================

                        .requestMatchers("/api/notifications/**")
                        .authenticated()


                        // =========================
                        // POST LOGIN
                        // =========================

                        .requestMatchers("/post-login")
                        .authenticated()


                        // =========================
                        // EVERYTHING ELSE
                        // =========================

                        .anyRequest().authenticated()
                )


                // =========================
                // LOGIN
                // =========================

                .formLogin(form -> form

                        .loginPage("/login")

                        .defaultSuccessUrl(
                                "/post-login",
                                true
                        )

                        .failureUrl(
                                "/login?error=true"
                        )

                        .permitAll()
                )


                // =========================
                // LOGOUT
                // =========================

                .logout(logout -> logout

                        .logoutUrl("/logout")

                        .logoutSuccessUrl(
                                "/login?logout=true"
                        )

                        .permitAll()
                );


        return http.build();
    }
}
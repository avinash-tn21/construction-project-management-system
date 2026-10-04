package com.example.construction.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostLoginController {

    @GetMapping("/post-login")
    public String postLogin(Authentication authentication) {

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"))) {

            return "redirect:/client/dashboard";
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CONTRACTOR"))) {

            return "redirect:/contractor/dashboard";
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROJECT_MANAGER"))) {

            return "redirect:/dashboard";
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SITE_SUPERVISOR"))) {

            return "redirect:/daily-logs";
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INVENTORY_MANAGER"))) {

            return "redirect:/inventory";
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EQUIPMENT_MANAGER"))) {

            return "redirect:/equipment";
        }

        // Fallback
        return "redirect:/dashboard";
    }
}
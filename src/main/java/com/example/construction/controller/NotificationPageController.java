package com.example.construction.controller;

import com.example.construction.entity.User;
import com.example.construction.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotificationPageController {

    private final UserRepository userRepository;

    public NotificationPageController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/notifications")
    public String notifications(Authentication authentication, Model model) {

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("currentUserId", user.getUserId());
        model.addAttribute("currentUserRole", user.getRole());

        return "notifications";
    }
}
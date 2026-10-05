package com.example.construction.controller;

import com.example.construction.entity.User;
import com.example.construction.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> getCurrentUserOnly(
            Authentication authentication) {

        return findCurrentUser(authentication)
                .map(user -> ResponseEntity.ok(List.of(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        return findCurrentUser(authentication)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private java.util.Optional<User> findCurrentUser(
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return java.util.Optional.empty();
        }

        return userRepository.findByUsername(authentication.getName());
    }
}

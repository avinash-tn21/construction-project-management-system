package com.example.construction.controller;

import com.example.construction.entity.Notification;
import com.example.construction.entity.User;
import com.example.construction.repository.UserRepository;
import com.example.construction.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(
            NotificationService notificationService,
            UserRepository userRepository) {

        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(
            @PathVariable Integer userId,
            org.springframework.security.core.Authentication authentication) {

        if (!isCurrentUser(userId, authentication)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(notificationService.getNotifications(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @PathVariable Integer userId,
            org.springframework.security.core.Authentication authentication) {

        if (!isCurrentUser(userId, authentication)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Integer id,
            org.springframework.security.core.Authentication authentication) {

        return notificationService.getNotificationById(id)
                .filter(notification -> isCurrentUser(
                        notification.getRecipientId(), authentication))
                .map(notification -> {
                    notificationService.markAsRead(notification);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private boolean isCurrentUser(Integer userId,
                                  org.springframework.security.core.Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && userRepository.findByUsername(authentication.getName())
                .map(User::getUserId)
                .filter(userId::equals)
                .isPresent();
    }


    public ResponseEntity<Notification> createTestNotification() {

        Notification notification =
                notificationService.createNotification(
                        3, // PM
                        4, // Equipment Manager
                        "Equipment Allocation Request",
                        "Project Manager requested an Excavator for Site A.",
                        "EQUIPMENT_REQUEST",
                        null
                );

        return ResponseEntity.ok(notification);
    }
}

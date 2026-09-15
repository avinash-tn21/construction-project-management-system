package com.example.construction.controller;

import com.example.construction.entity.Notification;
import com.example.construction.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    public List<Notification> getNotifications(
            @PathVariable Integer userId) {

        return notificationService.getNotifications(userId);
    }

    @GetMapping("/user/{userId}/unread")
    public List<Notification> getUnreadNotifications(
            @PathVariable Integer userId) {

        return notificationService
                .getUnreadNotifications(userId);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Integer id) {

        notificationService.markAsRead(id);

        return ResponseEntity.ok().build();
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
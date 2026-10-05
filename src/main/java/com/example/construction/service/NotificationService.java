package com.example.construction.service;

import com.example.construction.entity.Notification;
import com.example.construction.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {

        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getNotifications(Integer recipientId) {

        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    public List<Notification> getUnreadNotifications(
            Integer recipientId) {

        return notificationRepository
                .findByRecipientIdAndStatusOrderByCreatedAtDesc(
                        recipientId,
                        "UNREAD"
                );
    }

    public Optional<Notification> getNotificationById(Integer notificationId) {
        return notificationRepository.findById(notificationId);
    }

    public Notification createNotification(
            Integer senderId,
            Integer recipientId,
            String title,
            String message,
            String type,
            Integer relatedId) {

        Notification notification = new Notification();

        notification.setSenderId(senderId);
        notification.setRecipientId(recipientId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notification.setStatus("UNREAD");
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    public void markAsRead(Notification notification) {
        notification.setStatus("READ");
        notificationRepository.save(notification);
    }
}

package com.example.construction.repository;

import com.example.construction.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Integer> {

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(
            Integer recipientId
    );

    List<Notification> findByRecipientIdAndStatusOrderByCreatedAtDesc(
            Integer recipientId,
            String status
    );
}
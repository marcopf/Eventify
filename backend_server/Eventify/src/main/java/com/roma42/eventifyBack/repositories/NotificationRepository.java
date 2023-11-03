package com.roma42.eventifyBack.repositories;

import com.roma42.eventifyBack.models.Notification;
import com.roma42.eventifyBack.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);
    void deleteByUser(User user);
}

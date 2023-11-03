package com.roma42.eventifyBack.services;

import com.roma42.eventifyBack.repositories.NotificationRepository;
import com.roma42.eventifyBack.models.Category;
import com.roma42.eventifyBack.models.Event;
import com.roma42.eventifyBack.models.Notification;
import com.roma42.eventifyBack.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.StringJoiner;

@Service
public class NotificationService {
    final private NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification addNotification(Notification notification) {
        return this.notificationRepository.save(notification);
    }

    public Notification generateNotification(Event oldEvent, Event newEvent) {
        Notification notification = new Notification();
        notification.setEventId(oldEvent.getEventId());
        StringBuilder messageBuilder = new StringBuilder();
        if (!newEvent.getTitle().equals(oldEvent.getTitle()))
            messageBuilder.append("title: ")
                    .append(oldEvent.getTitle())
                    .append(" -> ")
                    .append(newEvent.getTitle())
                    .append("\n");
        if (!newEvent.getPlace().equals(oldEvent.getPlace()))
            messageBuilder.append("place: ")
                    .append(oldEvent.getPlace())
                    .append(" -> ")
                    .append(newEvent.getPlace())
                    .append("\n");
        if (!newEvent.getEventDate().equals(oldEvent.getEventDate()))
            messageBuilder.append("date: ")
                    .append(oldEvent.getEventDate().toString())
                    .append(" -> ")
                    .append(newEvent.getEventDate().toString())
                    .append("\n");
        notification.setMessage(messageBuilder.toString());
        return notification;
    }

    public Notification generateNotificationDeleted(Event event) {
        Notification notification = new Notification();
        notification.setEventId(-1L);
        String message = "Title: " + event.getTitle() + "\n" +
                "Place: " + event.getPlace() + "\n" +
                "Date: " + event.getEventDate().toString() + "\n";
        notification.setMessage(message);
        return notification;
    }

    public Notification generateNotificationCreateCategories(List<Category> categories) {
        Notification notification = new Notification();
        notification.setEventId(-1L);
        StringJoiner categoriesName = new StringJoiner(", ");
        categories.forEach((category) -> {
            categoriesName.add(category.getCategoryName());
        });
        String message = categoriesName + "\n";
        notification.setMessage(message);
        return notification;
    }

    public Notification generateNotificationDeleteCategory(String categoryName) {
        Notification notification = new Notification();
        notification.setEventId(-1L);
        String message = categoryName + "\n";
        notification.setMessage(message);
        return notification;
    }

    public Notification generateNotificationUpdateCategory(String oldCategoryName, String newCategoryName) {
        Notification notification = new Notification();
        notification.setEventId(-1L);
        String message = oldCategoryName + " -> " + newCategoryName + "\n";
        notification.setMessage(message);
        return notification;
    }

    public List<Notification> findNotificationByUser(User user) {
        return this.notificationRepository.findByUser(user);
    }

    public void deleteNotificationByUser(User user) {
        this.notificationRepository.deleteByUser(user);
    }
}

package com.roma42.eventifyBack.mappers;

import com.roma42.eventifyBack.dto.NotificationDto;
import com.roma42.eventifyBack.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationMapper {
    static public Notification toNotification(NotificationDto notificationDto) {
        Notification notification = new Notification();
        notification.setEventId(notificationDto.getEventId());
        notification.setMessage(notificationDto.getMessage());
        return notification;
    }

    static public NotificationDto toDto(Notification notification) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setEventId(notification.getEventId());
        notificationDto.setTitle("Hey Listen!!! You have a new notification");
        notificationDto.setMessage(notification.getMessage());
        return notificationDto;
    }

    static public List<NotificationDto> listToDtoList(List<Notification> notifications) {
        List<NotificationDto> notificationsDto = new ArrayList<>();
        for (Notification notification : notifications) {
            notificationsDto.add(toDto(notification));
        }
        return notificationsDto;
    }
}

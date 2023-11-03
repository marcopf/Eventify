package com.roma42.eventifyBack.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", updatable = false)
    private Long notificationId;

    @Column(name = "message", length = 1000)
    private String message;

    @Column(name = "event_id")
    private Long eventId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}

package com.roma42.eventifyBack.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@AllArgsConstructor
@Getter
@Setter
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "first_name", length = 64, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 64, nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "profile_picture", nullable = false)
    private String profilePicture;

    @Column(name = "date_of_birth", nullable = false, updatable = false)
    private LocalDate dateOfBirth;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    @Column(name = "password_code", length = 64)
    private String passwordCode;

    @Column(name = "upload_code", length = 64)
    private String uploadCode;
  
    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "loggedOut")
    private boolean loggedOut;

    @Column(name = "blocked")
    private boolean blocked;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private UserCredential userCredential;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Event> ownedEvents;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<ParticipantList> events;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private RolesList rolesList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private Set<Notification> notifications;

    public User() {
        ownedEvents = new HashSet<>();
        events = new HashSet<>();
        notifications = new HashSet<>();
    }

    public void appendEvent(ParticipantList event) {
        this.events.add(event);
    }

    public void removeEvent(ParticipantList event) {
        this.events.remove(event);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", verificationCode=" + verificationCode +
                ", enabled=" + enabled +
                ", loggedOut=" + loggedOut +
                ", dateOfBirth=" + dateOfBirth +
                ", roles_list=" + rolesList +
                ", ownedEvents=" + ownedEvents +
                ", events=" + events +
                ", userCredential=" + userCredential +
                ", notifications=" + notifications +
                '}';
    }
}

package com.roma42.eventifyBack.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "event")
@AllArgsConstructor
@Getter
@Setter
public class Event implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false, updatable = false)
    private Long eventId;

    @Column(name = "event_date", columnDefinition = "DATETIME(0)", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "description", length = 3000, nullable = false)
    private String description;

    @Column(name = "place", nullable = false)
    private String place;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "maps_url", length = 1000, nullable = false)
    private String mapsUrl;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ParticipantList> participants;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    Set<Picture> pictures;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<EventCategories> categories;

    public Event() {
        participants = new HashSet<>();
        pictures = new HashSet<>();
        categories = new HashSet<>();
    };

    public void addParticipant(ParticipantList participant) {
        this.participants.add(participant);
    }

    public void removeParticipant(ParticipantList participant) {
        this.participants.remove(participant);
    }

    public void removePicture(Picture picture) {
        this.pictures.remove(picture);
    }

    public void resetPictures() {
        this.pictures.clear();
    }

    public void substituteCategories(Set<EventCategories> eventCategoriesSet) {
        this.categories.clear();
        this.categories.addAll(eventCategoriesSet);
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", eventDate=" + eventDate +
                ", description='" + description + '\'' +
                ", place='" + place + '\'' +
                ", title='" + title + '\'' +
                ", mapsUrl='" + mapsUrl + '\'' +
                ", user=" + user +
                ", participants=" + participants +
                ", pictures=" + pictures +
                ", categories=" + categories +
                '}';
    }

    public boolean modified(Event oldEvent) {
        return (!this.getEventDate().equals(oldEvent.getEventDate())
                || !this.getPlace().equals(oldEvent.getPlace())
                || !this.getTitle().equals(oldEvent.getTitle()));
    }
}

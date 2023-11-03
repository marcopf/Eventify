package com.roma42.eventifyBack.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "participant_list")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParticipantList {
    @EmbeddedId
    private ParticipantListId participantListId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    @JsonIgnore
    private Event event;

    @Override
    public String toString() {
        return "ParticipantList{" +
                "participantListId=" + participantListId +
                ", user=" + user +
                ", event=" + event +
                '}';
    }
}

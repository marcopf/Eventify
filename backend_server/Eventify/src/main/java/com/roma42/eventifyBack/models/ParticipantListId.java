package com.roma42.eventifyBack.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParticipantListId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "event_id")
    private Long eventId;

    @Override
    public String toString() {
        return "ParticipantListId{" +
                "userId=" + userId +
                ", eventId=" + eventId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipantListId that)) return false;
        return Objects.equals(getUserId(), that.getUserId()) && Objects.equals(getEventId(), that.getEventId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getEventId());
    }
}

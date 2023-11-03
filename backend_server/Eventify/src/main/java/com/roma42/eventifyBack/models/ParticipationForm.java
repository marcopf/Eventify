package com.roma42.eventifyBack.models;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParticipationForm implements Serializable {
    private String username;
    private Long eventId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipationForm that)) return false;
        return Objects.equals(getUsername(), that.getUsername()) && Objects.equals(getEventId(), that.getEventId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getEventId());
    }

    @Override
    public String toString() {
        return "ParticipationForm{" +
                "username='" + username + '\'' +
                ", eventId=" + eventId +
                '}';
    }
}

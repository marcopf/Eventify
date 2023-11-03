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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventCategoriesId implements Serializable {
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "category_id")
    private Long categoryId;

    @Override
    public String toString() {
        return "EventCategoriesId{" +
                "eventId=" + eventId +
                ", categoryId=" + categoryId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventCategoriesId that)) return false;
        return Objects.equals(getEventId(), that.getEventId()) && Objects.equals(getCategoryId(), that.getCategoryId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEventId(), getCategoryId());
    }
}

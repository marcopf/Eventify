package com.roma42.eventifyBack.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "event_categories")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventCategories {

    @EmbeddedId
    private EventCategoriesId eventCategoriesId;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @MapsId("eventId")
    @JsonIgnore
    private Event event;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @MapsId("categoryId")
    @JsonIgnore
    private Category category;

    @Override
    public String toString() {
        return "EventCategories{" +
                "eventCategoriesId=" + eventCategoriesId +
                ", event=" + event +
                ", category=" + category +
                '}';
    }
}
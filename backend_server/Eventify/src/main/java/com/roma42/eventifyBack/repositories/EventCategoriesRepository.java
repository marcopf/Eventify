package com.roma42.eventifyBack.repositories;

import com.roma42.eventifyBack.models.EventCategoriesId;
import com.roma42.eventifyBack.models.Event;
import com.roma42.eventifyBack.models.EventCategories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCategoriesRepository extends JpaRepository<EventCategories, EventCategoriesId> {
    void deleteByEvent(Event event);
}

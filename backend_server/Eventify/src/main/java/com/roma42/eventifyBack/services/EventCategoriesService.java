package com.roma42.eventifyBack.services;

import com.roma42.eventifyBack.models.EventCategoriesId;
import com.roma42.eventifyBack.repositories.EventCategoriesRepository;
import com.roma42.eventifyBack.models.Category;
import com.roma42.eventifyBack.models.Event;
import com.roma42.eventifyBack.models.EventCategories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class EventCategoriesService {

    final private EventCategoriesRepository eventCategoriesRepository;

    @Autowired
    public EventCategoriesService(EventCategoriesRepository eventCategoriesRepository) {
        this.eventCategoriesRepository = eventCategoriesRepository;
    }

    public void deleteEventCategoriesByEvent(Event event) {
        this.eventCategoriesRepository.deleteByEvent(event);
    }

    static public Set<EventCategories> categoriesToEventCategories(Set<Category> categories, Event event) {
        Set<EventCategories> eventCategories = new HashSet<>();
        for (Category category : categories)
            eventCategories.add(new EventCategories(new EventCategoriesId(event.getEventId(), category.getCategoryId()),
                    event, category));
        return eventCategories;
    }
}

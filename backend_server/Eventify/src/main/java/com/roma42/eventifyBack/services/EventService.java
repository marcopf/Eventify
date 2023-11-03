package com.roma42.eventifyBack.services;

import com.roma42.eventifyBack.exception.EmptyPageException;
import com.roma42.eventifyBack.exception.EventNotFoundException;
import com.roma42.eventifyBack.exception.UnsupportedTypeException;
import com.roma42.eventifyBack.repositories.EventRepository;
import com.roma42.eventifyBack.models.Category;
import com.roma42.eventifyBack.models.Event;
import com.roma42.eventifyBack.models.ParticipantList;
import com.roma42.eventifyBack.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class EventService {

    final private EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> findAllEvent() {
        return this.eventRepository.findAll();
    }

    public Event findEventById(Long id) {
        return this.eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
    }

    public Slice<Event> findSliceBy(Pageable pageable) {
        return this.eventRepository.findSliceBy(pageable);
    }

    public Slice<Event> findSliceByPlaceStartingWith(String search, Pageable pageable) {
        if (search.isEmpty())
            throw new EmptyPageException("empty page");
        return this.eventRepository.findSliceByPlaceContainsIgnoreCase(search, pageable);
    }

    public Slice<Event> findSliceByTitleStartingWith(String search, Pageable pageable) {
        if (search.isEmpty())
            throw new EmptyPageException("empty page");
        return this.eventRepository.findSliceByTitleContainsIgnoreCase(search, pageable);
    }

    public Slice<Event> findSliceByEventDateBetween(String from, String to, Pageable pageable) {
        if (from.isEmpty() && to.isEmpty())
            throw new EmptyPageException("empty page");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        if (to.isEmpty()) {
            LocalDateTime fromDate = LocalDateTime.parse(from, formatter);
            return this.eventRepository.findSliceByEventDateAfter(fromDate, pageable);
        } else if (from.isEmpty()) {
            LocalDateTime toDate = LocalDateTime.parse(to, formatter);
            return this.eventRepository.findSliceByEventDateBefore(toDate, pageable);
        }
        LocalDateTime fromDate = LocalDateTime.parse(from, formatter);
        LocalDateTime toDate = LocalDateTime.parse(to, formatter);
        return this.eventRepository.findSliceByEventDateBetween(fromDate, toDate, pageable);
    }

    public Slice<Event> findSliceByOwnerId(User user, Pageable pageable) {
        Pageable pageableSort = PageRequest
                .of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("eventDate").ascending());
        return this.eventRepository.findSliceByUser(user, pageableSort);
    }

    public Slice<Event> findByCategories(Set<String> categories, Pageable pageable) {
        if (categories == null || categories.isEmpty())
            throw new EmptyPageException("empty page");
        return this.eventRepository.findByCategoriesInSet(categories, (long) categories.size());
    }

    public Slice<Event> searchEngine(Map<String, String> params, Set<String> categories, Pageable pageable) {
        Slice<Event> events;

        Pageable pageableSort = PageRequest
                .of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("eventDate").ascending());
        if (params.get("type").isEmpty()) {
            events = findSliceBy(pageableSort);
        } else {
            events = switch (params.get("type")) {
                case "place" -> findSliceByPlaceStartingWith(params.get("search"), pageableSort);
                case "title" -> findSliceByTitleStartingWith(params.get("search"), pageableSort);
                case "time" -> findSliceByEventDateBetween(params.get("from"), params.get("to"), pageableSort);
                case "category" -> findByCategories(categories, pageableSort);
                default -> throw new UnsupportedTypeException("cannot resolve search");
            };
        }
        if (events.isEmpty())
            throw  new EmptyPageException("empty page");
        return events;
    }

    public Event addEvent(Event event, ParticipantList participantList, Set<Category> categories) {
        Event savedEvent = this.eventRepository.save(event);
        savedEvent.addParticipant(participantList);
        savedEvent.substituteCategories(EventCategoriesService.categoriesToEventCategories(categories, savedEvent));
        return this.eventRepository.save(savedEvent);
    }

    public Event updateEventWithSent(Event foundEvent, Event sentEvent) {
        foundEvent.setTitle(sentEvent.getTitle());
        foundEvent.setDescription(sentEvent.getDescription());
        foundEvent.setPlace(sentEvent.getPlace());
        foundEvent.setMapsUrl(sentEvent.getMapsUrl());
        foundEvent.setEventDate(sentEvent.getEventDate());
        return this.eventRepository.save(foundEvent);
    }

    public Event updateEvent(Event event) {
        return this.eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        this.eventRepository.deleteById(id);
    }

    public void deleteEventByEventDateBeforeNow() {
        this.eventRepository.deleteByEventDateBefore(LocalDateTime.now());
    }
}

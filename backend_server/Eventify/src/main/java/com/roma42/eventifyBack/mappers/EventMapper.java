package com.roma42.eventifyBack.mappers;

import com.roma42.eventifyBack.dto.EventDto;
import com.roma42.eventifyBack.services.MapService;
import com.roma42.eventifyBack.models.Event;
import com.roma42.eventifyBack.models.EventCategories;
import com.roma42.eventifyBack.models.ParticipantList;
import com.google.maps.errors.ApiException;
import org.springframework.data.domain.Slice;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventMapper {

    static public Event dtoToEvent(EventDto eventDto) throws IOException, ApiException, InterruptedException {
        Event event = new Event();
//        event.setEventId(eventDto.getId());
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setPlace(eventDto.getPlace());
        event.setMapsUrl(MapService.makeRequest(eventDto.getPlace()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        event.setEventDate(LocalDateTime.parse(eventDto.getEventDate(), formatter));
        return event;
    }

    static public EventDto eventToDMinimalDto(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getEventId());
        eventDto.setTitle(event.getTitle());
        eventDto.setEventDate(event.getEventDate().toString());
        eventDto.setDescription(event.getDescription());
        Set<String> categories = new HashSet<>();
        for (EventCategories ec : event.getCategories())
            categories.add(ec.getCategory().getCategoryName());
        eventDto.setCategories(categories);
        eventDto.setImages(null);
        eventDto.setParticipants(null);
        eventDto.setDescription(event.getDescription());
        return eventDto;
    }

    static public EventDto eventToDto(Event event, String username) {
        EventDto eventDto = eventToDMinimalDto(event);
        eventDto.setDescription(event.getDescription());
        eventDto.setPlace(event.getPlace());
        eventDto.setMapsUrl(event.getMapsUrl());
        eventDto.setOwner(event.getUser().getUserCredential().getUsername());
        // participant list should be returned in a different object?
        Set<String> participants = new HashSet<>();
        for (ParticipantList participant : event.getParticipants()) {
            participants.add(participant.getUser().getUserCredential().getUsername());
        }
        eventDto.setParticipants(participants);
        eventDto.setNumImages((long) event.getPictures().size());
        eventDto.setStatus(participants.contains(username));
        return eventDto;
    }

    static public List<EventDto> eventSliceToMinimalDtoList(Slice<Event> events) {
        List<EventDto> eventDtoList = new ArrayList<>();
        for (Event event : events)
            eventDtoList.add(eventToDMinimalDto(event));
        return eventDtoList;
    }

    static public List<EventDto> eventListToMinimalDtoList(List<Event> events) {
        List<EventDto> eventDtoList = new ArrayList<>();
        for (Event event : events)
            eventDtoList.add(eventToDMinimalDto(event));
        return eventDtoList;
    }
}

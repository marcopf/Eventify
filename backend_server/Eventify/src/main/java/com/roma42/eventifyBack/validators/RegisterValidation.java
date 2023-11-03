package com.roma42.eventifyBack.validators;

import com.roma42.eventifyBack.exception.ForbiddenException;
import com.roma42.eventifyBack.models.Event;
import com.roma42.eventifyBack.models.ParticipantList;
import com.roma42.eventifyBack.models.User;

public class RegisterValidation {
    static public void validateRegistration(User user, Event event) {
        if (user.equals(event.getUser()))
            throw new ForbiddenException("Owner is already registered");
        for (ParticipantList pl : user.getEvents()) {
            if (pl.getParticipantListId().getEventId().equals(event.getEventId()))
                throw new ForbiddenException("Cannot register again at this event");
        }
    }

    static public void validateUnsubscribe(User user, Event event) {
        if (user.equals(event.getUser()))
            throw new ForbiddenException("Owner cannot unsubscribe from owned event");
    }
}

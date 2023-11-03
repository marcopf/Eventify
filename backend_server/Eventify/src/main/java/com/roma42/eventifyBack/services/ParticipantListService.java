package com.roma42.eventifyBack.services;

import com.roma42.eventifyBack.exception.ParticipantListNotFoundException;
import com.roma42.eventifyBack.repositories.ParticipantListRepository;
import com.roma42.eventifyBack.models.Event;
import com.roma42.eventifyBack.models.ParticipantList;
import com.roma42.eventifyBack.models.ParticipantListId;
import com.roma42.eventifyBack.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantListService {
    final private ParticipantListRepository participantListRepository;

    @Autowired
    public ParticipantListService(ParticipantListRepository participantListRepository) {
        this.participantListRepository = participantListRepository;
    }

    public List<ParticipantList> findAllParticipantList() {
        return this.participantListRepository.findAll();
    }

    public ParticipantList findParticipantListByParticipantListId(ParticipantListId participantListId) {
        return this.participantListRepository.findParticipantListByParticipantListId(participantListId)
                .orElseThrow(() -> new ParticipantListNotFoundException("Participant list not found"));
    }

    static public ParticipantList createMockParticipantList(User user, Event event) {
        ParticipantList participantList = new ParticipantList();
        participantList.setUser(user);
        participantList.setEvent(event);
        participantList.setParticipantListId(new ParticipantListId(user.getId(), event.getEventId()));
        return participantList;
    }

    public ParticipantList addParticipantList(ParticipantList participantList) {
        return this.participantListRepository.save(participantList);
    }

    public ParticipantList updateParticipantList(ParticipantList participantList) {
        return this.participantListRepository.save(participantList);
    }

    public void deleteParticipantList(ParticipantListId participantListId) {
        this.participantListRepository.deleteParticipantListByParticipantListId(participantListId);
    }
}
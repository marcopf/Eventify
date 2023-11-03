package com.roma42.eventifyBack.repositories;

import com.roma42.eventifyBack.models.ParticipantList;
import com.roma42.eventifyBack.models.ParticipantListId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantListRepository extends JpaRepository<ParticipantList, ParticipantListId> {
    public Optional<ParticipantList> findParticipantListByParticipantListId(ParticipantListId participantListId);
    public void deleteParticipantListByParticipantListId(ParticipantListId participantListId);
}

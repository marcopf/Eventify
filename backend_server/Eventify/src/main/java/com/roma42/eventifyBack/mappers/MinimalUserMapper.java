package com.roma42.eventifyBack.mappers;

import com.roma42.eventifyBack.dto.MinimalUserDto;
import com.roma42.eventifyBack.models.User;

public class MinimalUserMapper {

    static public MinimalUserDto toDto(User user) {
        MinimalUserDto userDto = new MinimalUserDto();
        userDto.setPicture(user.getProfilePicture());
        userDto.setUsername(user.getUserCredential().getUsername());
        userDto.setCreatedEvents((long) user.getOwnedEvents().size());
        userDto.setSubscribedEvents((long) user.getEvents().size());
        userDto.setVerified(user.isEnabled());
        userDto.setBlocked(user.isBlocked());

        return userDto;
    }
}

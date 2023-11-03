package com.roma42.eventifyBack.mappers;

import com.roma42.eventifyBack.dto.UserDto;
import com.roma42.eventifyBack.models.User;
import com.roma42.eventifyBack.models.UserCredential;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserMapper {

    static public User toUser(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        UserCredential credential = new UserCredential();
        credential.setUsername(userDto.getUsername());
        credential.setPassword(userDto.getPassword());
        user.setUserCredential(credential);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        user.setDateOfBirth(LocalDate.parse(userDto.getBirthOfDate(), formatter));
        user.setProfilePicture("");
        user.setRolesList(null);

        return user;
    }

    static public User toUserWithoutDate(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        UserCredential credential = new UserCredential();
        credential.setUsername(userDto.getUsername());
        credential.setPassword(userDto.getPassword());
        user.setUserCredential(credential);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        user.setDateOfBirth(LocalDate.parse(userDto.getBirthOfDate(), formatter));
        user.setProfilePicture("");
        user.setRolesList(null);
        user.setEnabled(true);

        return user;
    }
    static public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUserCredential().getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setBirthOfDate(user.getDateOfBirth().toString());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}

package com.roma42.eventifyBack.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MinimalUserDto implements Serializable {
    private String username;

    private String picture;

    private Long subscribedEvents;

    private Long createdEvents;

    private Boolean verified;

    private Boolean blocked;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MinimalUserDto userDto)) return false;
        return Objects.equals(getUsername(), userDto.getUsername())
                && Objects.equals(getPicture(), userDto.getPicture())
                && Objects.equals(getSubscribedEvents(), userDto.getSubscribedEvents())
                && Objects.equals(getCreatedEvents(), userDto.getCreatedEvents())
                && Objects.equals(getVerified(), userDto.getVerified())
                && Objects.equals(getBlocked(), userDto.getBlocked());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(),
                getPicture(),
                getSubscribedEvents(),
                getCreatedEvents(),
                getVerified(),
                getBlocked());
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "username='" + username + '\'' +
                ", picture='" + picture + '\'' +
                ", subscribedEvents=" + subscribedEvents +
                ", createdEvents=" + createdEvents +
                ", verified=" + verified +
                ", blocked=" + blocked +
                '}';
    }
}
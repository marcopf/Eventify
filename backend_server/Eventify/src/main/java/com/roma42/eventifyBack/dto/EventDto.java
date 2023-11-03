package com.roma42.eventifyBack.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Getter
@Setter
public class EventDto implements Serializable {

    private Long id;
    @Pattern(regexp = "^[a-zA-Z0-9!@$_,. -]{3,255}$",
            message = "invalid title")
    private String title;
    @Pattern(regexp = "^[a-zA-Z0-9!@$_-]{3,32}$",
            message = "invalid username")
    private String owner;
//    @Pattern(regexp = "^[a-zA-Z0=9!?@$:;\"'.,_-]{3,3000}$",
//            message = "invalid description")
    private String description;
    @Pattern(regexp = "^[a-zA-Z0-9,. +-]{3,255}$",
            message = "invalid place")
    private String place;
    private String mapsUrl;
    @Pattern(regexp = "^[0-9T:-]{16}",
            message = "invalid date")
    private String eventDate;
    private Long numImages;
    private boolean status;
    private Set<String> images;
    private Set<String> categories;
    private Set<String> participants;
    private boolean userParticipation;

    public EventDto() {
        images = new HashSet<>();
        categories = new HashSet<>();
        participants = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventDto eventDto)) return false;
        return Objects.equals(getId(), eventDto.getId())
                && Objects.equals(getTitle(), eventDto.getTitle())
                && Objects.equals(getOwner(), eventDto.getOwner())
                && Objects.equals(getDescription(), eventDto.getDescription())
                && Objects.equals(getPlace(), eventDto.getPlace())
                && Objects.equals(getMapsUrl(), eventDto.getMapsUrl())
                && Objects.equals(getEventDate(), eventDto.getEventDate())
                && Objects.equals(getImages(), eventDto.getImages())
                && Objects.equals(getCategories(), eventDto.getCategories())
                && Objects.equals(getParticipants(), eventDto.getParticipants());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getOwner(), getDescription(), getPlace(), getMapsUrl(), getEventDate(),
                getImages(), getCategories(), getParticipants());
    }

    @Override
    public String toString() {
        return "EventDto{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", owner'=" + owner + '\'' +
                ", description='" + description + '\'' +
                ", place='" + place + '\'' +
                ", mapsUrl='" + mapsUrl + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", images=" + images +
                ", categories=" + categories +
                ", participants=" + participants +
                '}';
    }
}

package com.roma42.eventifyBack.dto;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
public class SearchResultDto implements Serializable {
    private Boolean hasNextPage;
    private List<EventDto> events;

    public SearchResultDto() {
        events = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "searchResultDto{" +
                "hasNextPage=" + hasNextPage +
                ", events=" + events +
                '}';
    }
}

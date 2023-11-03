package com.roma42.eventifyBack.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Location implements Serializable {
    @JsonProperty("lat")
    private String latitude;
    @JsonProperty("lng")
    private String longitude;

    public String getCoordinates() {
        return latitude + "," + longitude;
    }
}

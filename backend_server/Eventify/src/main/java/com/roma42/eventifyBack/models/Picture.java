package com.roma42.eventifyBack.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "picture")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "pictureId")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Picture implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "picture_id", nullable = false)
    private Long pictureId;

    @Column(name = "uri", nullable = false, updatable = false)
    private String uri;

//    @Column(name = "event_id", nullable = false, updatable = false)
//    private Long eventId;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonIgnore
    private Event event;

    @Override
    public String toString() {
        return "Picture{" +
                "pictureId=" + pictureId +
                ", uri='" + uri + '\'' +
//                ", eventId=" + eventId +
                ", event=" + event +
                '}';
    }
}

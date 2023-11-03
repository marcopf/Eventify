package com.roma42.eventifyBack.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "roles_list")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
@NoArgsConstructor
@Getter
@Setter
public class RolesList implements Serializable {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @MapsId
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public RolesList(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "RolesList{" +
                "userId=" + userId +
                ", user=" + user +
                ", role=" + role +
                '}';
    }
}

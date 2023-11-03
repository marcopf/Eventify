package com.roma42.eventifyBack.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "user_credential")
@NoArgsConstructor
@Getter
@Setter
public class UserCredential implements Serializable {
    @Id
    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "username", length = 32, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @MapsId
    @JsonIgnore
    private User user;

    public UserCredential(Long user_id, String username, String password) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserCredential{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

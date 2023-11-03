package com.roma42.eventifyBack.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginDto implements Serializable {

    private String username;

    private String password;

    @Override
    public String toString() {
        return "LoginDto{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginDto loginDto)) return false;
        return Objects.equals(getUsername(), loginDto.getUsername())
                && Objects.equals(getPassword(), loginDto.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword());
    }
}

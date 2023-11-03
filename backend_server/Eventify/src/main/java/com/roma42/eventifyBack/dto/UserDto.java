package com.roma42.eventifyBack.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto implements Serializable {

    @Pattern(regexp = "^[a-zA-Z0-9!@$_-]{3,32}$",
            message = "invalid username")
    private String username;

    private String password;

    private String newPassword;

    @Pattern(regexp = "^[a-zA-Z ]+$",
            message = "invalid name")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z ]+$",
            message = "invalid surname")
    private String lastName;

    @Pattern(regexp = "(?:[a-z0-9!#$%&'*+=?^_{|}~-]+(?:\\.[a-z0-9!#$%&'*+=?^_{|}~-]+)*|\"" +
            "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-" +
            "\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+" +
            "[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-" +
            "9]?[0-9])\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]" +
            "*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\" +
            "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])",
            message = "invalid email")
    private String email;

    @Pattern(regexp = "^[0-9-]{10}",
            message = "invalid date of birth")
    private String birthOfDate;

    @Override
    public String toString() {
        return "SignUpDto{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", newPassword='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", birthOfDate='" + birthOfDate + '\'' +
                '}';
    }

    public String uploadCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDto signUpDto)) return false;
        return Objects.equals(getUsername(), signUpDto.getUsername())
                && Objects.equals(getPassword(), signUpDto.getPassword())
                && Objects.equals(getNewPassword(), signUpDto.getNewPassword())
                && Objects.equals(getFirstName(), signUpDto.getFirstName())
                && Objects.equals(getLastName(), signUpDto.getLastName())
                && Objects.equals(getEmail(), signUpDto.getEmail())
                && Objects.equals(getUploadCode(), signUpDto.getUploadCode())
                && Objects.equals(getBirthOfDate(), signUpDto.getBirthOfDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword(), getNewPassword(), getFirstName(), getLastName(),
                getEmail(), getBirthOfDate(), getUploadCode());
    }
}

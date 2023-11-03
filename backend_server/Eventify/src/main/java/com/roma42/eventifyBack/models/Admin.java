package com.roma42.eventifyBack.models;

import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Admin {
    @Pattern(regexp = "^[a-zA-Z0-9!@$_-]{3,32}$",
            message = "invalid username")
    private String username;
    @Pattern(regexp = "^[a-zA-Z ]+$",
            message = "invalid name")
    private String firstName;    @Pattern(regexp = "^[a-zA-Z ]+$",
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
    private String dateOfBirth;
    private String password;
    private String image;
}

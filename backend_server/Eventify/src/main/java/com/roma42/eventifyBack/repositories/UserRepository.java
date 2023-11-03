package com.roma42.eventifyBack.repositories;

import com.roma42.eventifyBack.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByVerificationCode(String verificationCode);
    Optional<User> findUserByUploadCode(String verificationCode);
    Optional<User> findUserByPasswordCode(String passwordCode);
    Optional<User> findByEmail(String email);
}

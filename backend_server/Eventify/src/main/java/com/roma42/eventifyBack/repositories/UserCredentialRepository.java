package com.roma42.eventifyBack.repositories;

import com.roma42.eventifyBack.models.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    public Optional<UserCredential> findByUsername(String username);
}

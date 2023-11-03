package com.roma42.eventifyBack.repositories;

import com.roma42.eventifyBack.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}

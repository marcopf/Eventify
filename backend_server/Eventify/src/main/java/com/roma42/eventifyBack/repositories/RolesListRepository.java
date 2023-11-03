package com.roma42.eventifyBack.repositories;

import com.roma42.eventifyBack.models.Role;
import com.roma42.eventifyBack.models.RolesList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesListRepository extends JpaRepository<RolesList, Long> {
    Optional<RolesList> findByRole(Role role);
}

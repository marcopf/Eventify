package com.roma42.eventifyBack.services;

import com.roma42.eventifyBack.models.Role;
import com.roma42.eventifyBack.repositories.RolesListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolesListService {

    final private RolesListRepository rolesListRepository;

    @Autowired
    public RolesListService(RolesListRepository rolesListRepository) {
        this.rolesListRepository = rolesListRepository;
    }

    public boolean isUserWithRole(Role role) {
        return this.rolesListRepository.findByRole(role).isPresent();
    }
}

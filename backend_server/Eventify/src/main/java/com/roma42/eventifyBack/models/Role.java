package com.roma42.eventifyBack.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<RolesList> rolesList;

    public Role() {
        rolesList = new HashSet<>();
    }

    public Role(Long id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", roleName=" + roleName +
                ", rolesList=" + rolesList +
                '}';
    }
}

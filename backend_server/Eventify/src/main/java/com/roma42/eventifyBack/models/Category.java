package com.roma42.eventifyBack.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category")
@AllArgsConstructor
@Getter
@Setter
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false, updatable = false)
    private Long categoryId;

    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<EventCategories> events;

    public Category() {
        events = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", events='" + events + '\'' +
                '}';
    }
}

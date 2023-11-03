package com.roma42.eventifyBack.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class CategoriesList implements Serializable {
    private List<String> categories;
    public CategoriesList() {
        categories = new ArrayList<>();
    }
}
package com.roma42.eventifyBack.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@Getter
@Setter
public class EditCategoriesForm implements Serializable {
    private List<String> toEdit;

    public EditCategoriesForm() {
        toEdit = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EditCategoriesForm that)) return false;
        return Objects.equals(getToEdit(), that.getToEdit());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getToEdit());
    }

    @Override
    public String toString() {
        return "EditCategoriesForm{" +
                "toEdit=" + toEdit +
                '}';
    }
}

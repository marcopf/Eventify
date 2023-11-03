package com.roma42.eventifyBack.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Getter
@Setter
public class CategoryDto implements Serializable {

    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,32}$")
    private String categoryName;
    private List<Long> events;

    public CategoryDto() {
        this.events = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryDto that)) return false;
        return Objects.equals(getCategoryName(), that.getCategoryName()) && Objects.equals(getEvents(), that.getEvents());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCategoryName(), getEvents());
    }

    @Override
    public String toString() {
        return "CategoryDto{" +
                "categoryName='" + categoryName + '\'' +
                ", events=" + events +
                '}';
    }
}

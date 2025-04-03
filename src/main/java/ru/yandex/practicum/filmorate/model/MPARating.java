package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MPARating {
    private Integer id;
    private String name;
    private String description;

    public MPARating(Integer id) {
        this.id = id;
    }
}

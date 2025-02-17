package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class Film {
    private int id;

    @NotEmpty(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не может превышать 200 символов")
    private String description;

    @NonNull
    @AssertTrue(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
}

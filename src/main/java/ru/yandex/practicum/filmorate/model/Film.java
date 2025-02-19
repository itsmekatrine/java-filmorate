package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private Integer id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не может превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не может быть пустой")
    private LocalDate releaseDate;

    @AssertTrue(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    public boolean isValidReleaseDate() {
        LocalDate earliestDate = LocalDate.of(1895, 12, 28);
        return releaseDate == null || !releaseDate.isBefore(earliestDate);
    }

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;
}

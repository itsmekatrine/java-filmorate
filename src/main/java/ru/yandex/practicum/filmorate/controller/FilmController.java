package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        film.setId(filmId++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            throw new IllegalArgumentException("Фильм с таким ID не найден");
        }
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }
}

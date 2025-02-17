package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Добавление нового фильма: {}", film);
        film.setId(filmId++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Ошибка обновления: фильм с ID {} не найден", film.getId());
            throw new ValidationException("Фильм с таким ID не найден");
        }
        log.info("Обновление фильма: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }
}

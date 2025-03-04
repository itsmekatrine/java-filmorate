package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 1;
    private static final LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public Film addFilm(Film film) {
        validateReleaseDate(film.getReleaseDate());
        log.info("Добавление нового фильма: {}", film);
        film.setId(filmId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validateReleaseDate(film.getReleaseDate());
        if (!films.containsKey(film.getId())) {
            log.error("Ошибка обновления: фильм с ID {} не найден", film.getId());
            throw new ValidationException("Фильм с таким ID не найден");
        }
        log.info("Обновление фильма: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate != null && releaseDate.isBefore(EARLIEST_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    @Override
    public Optional<Film> getFilmById(int filmId) {
        return Optional.ofNullable(films.get(filmId));
    }
}

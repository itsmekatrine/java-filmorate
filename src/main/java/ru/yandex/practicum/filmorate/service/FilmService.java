package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage,
                       MpaStorage mpaStorage,
                       GenreStorage genreStorage,
                       UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        log.info("Запрос на добавление фильма: {}", film);

        LocalDate minDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(minDate)) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ValidationException("Поле 'mpa' обязательно и должно содержать id");
        }
        MPARating mpa = mpaStorage.findById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("MPA с id " + film.getMpa().getId() + " не найден"));
        film.setMpa(mpa);

        if (film.getGenres() != null) {
            Set<Integer> requestedIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            Set<Genre> foundGenres = genreStorage.getByIds(requestedIds);

            if (foundGenres.size() != requestedIds.size()) {
                throw new NotFoundException("Один или несколько жанров не найдены: " + requestedIds);
            }
            film.setGenres(foundGenres);
        }

        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (filmStorage.getFilmById(film.getId()).isEmpty()) {
            throw new NoSuchElementException("Фильм с ID " + film.getId() + " не найден");
        }
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(int filmId, int userId) {
        filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NoSuchElementException("Фильм с ID " + filmId + " не найден"));

        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID " + userId + " не найден"));

        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(int countFilms) {
        List<Film> films = new ArrayList<>(filmStorage.getAllFilms());
        films.sort((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()));
        return films.subList(0, Math.min(countFilms, films.size()));
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new ValidationException("Фильм с ID " + filmId + " не найден"));
    }
}

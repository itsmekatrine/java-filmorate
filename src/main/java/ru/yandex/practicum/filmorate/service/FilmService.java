package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
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
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NoSuchElementException("Фильм с ID " + filmId + " не найден"));

        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID " + userId + " не найден"));

        film.getLikes().add(userId);
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

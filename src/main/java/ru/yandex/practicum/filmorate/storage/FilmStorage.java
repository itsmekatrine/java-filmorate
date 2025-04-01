package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> getAllFilms();

    Optional<Film> getFilmById(int filmId);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Set<Integer> getLikes(int filmId);
}

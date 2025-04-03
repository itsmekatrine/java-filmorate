package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmMapper;

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbc.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", film.getId(), genre.getId());
            }
        }
        film.setGenres(findGenresByFilmId(film.getId()));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbc.update(sql,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getName(),
                film.getId());

        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        if (film.getGenres() != null) {
            insertGenres(film.getId(), film.getGenres());
        }

        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = """
           SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id,
                m.id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description
           FROM films f
           LEFT JOIN mpa m ON f.mpa_id = m.id
        """;
        List<Film> films = jdbc.query(sql, filmMapper);

        Map<Integer, Set<Genre>> genresByFilmId = findGenresForFilms(films);
        for (Film film : films) {
            film.setGenres(genresByFilmId.getOrDefault(film.getId(), new LinkedHashSet<>()));
            film.setLikes(getLikes(film.getId()));
        }
        return films;
    }

    @Override
    public Optional<Film> getFilmById(int filmId) {
        String sql = """
        SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id,
               m.id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description
        FROM films f
        LEFT JOIN mpa m ON f.mpa_id = m.id
        WHERE f.id = ?
        """;
        try {
            Film film = jdbc.queryForObject(sql, filmMapper, filmId);
            if (film != null) {
                film.setGenres(findGenresByFilmId(film.getId()));
            }
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private void insertGenres(int filmId, Set<Genre> genres) {
        String sql = "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : genres) {
            jdbc.update(sql, filmId, genre.getId());
        }
    }

    private Set<Genre> findGenresByFilmId(int filmId) {
        String sql = "SELECT g.id, g.name FROM genres g " +
                "JOIN film_genres fg ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ?" +
                "ORDER BY g.id";
        List<Genre> genres = jdbc.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
        return new LinkedHashSet<>(genres);
    }

    private Map<Integer, Set<Genre>> findGenresForFilms(List<Film> films) {
        Map<Integer, Set<Genre>> genresByFilm = new HashMap<>();

        if (films.isEmpty()) {
            return genresByFilm;
        }

        List<Integer> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        String placeholders = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = "SELECT fg.film_id, g.id, g.name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fg.film_id IN (" + placeholders + ") " +
                "ORDER BY g.id";
        jdbc.query(sql, filmIds.toArray(), rs -> {
            while (rs.next()) {
                int filmId = rs.getInt("film_id");
                Genre genre = new Genre();
                genre.setId(rs.getInt("id"));
                genre.setName(rs.getString("name"));
                genresByFilm
                        .computeIfAbsent(filmId, id -> new LinkedHashSet<>())
                        .add(genre);
            }
        });
        return genresByFilm;
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "MERGE INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbc.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbc.update(sql, filmId, userId);
    }

    @Override
    public Set<Integer> getLikes(int filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbc.query(sql, (rs, rowNum) -> rs.getInt("user_id"), filmId));
    }
}

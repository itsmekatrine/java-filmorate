package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Test
    public void testGetFilmById() {
        Optional<Film> filmOptional = filmStorage.getFilmById(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testGetAllFilms() {
        Collection<Film> films = filmStorage.getAllFilms();

        assertThat(films).isNotEmpty();
    }

    @Test
    public void testAddFilm() {
        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("A sci-fi action film");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpa(new MPARating(1, "G", "У фильма нет возрастных ограничений"));

        Film created = filmStorage.addFilm(film);

        assertThat(created.getId()).isNotNull();

        Optional<Film> found = filmStorage.getFilmById(created.getId());
        assertThat(found).isPresent();
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film();
        film.setName("Old Title");
        film.setDescription("Old Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new MPARating(1, null, null));

        Film saved = filmStorage.addFilm(film);

        saved.setName("New Title");
        saved.setDescription("Updated Description");
        filmStorage.updateFilm(saved);

        Optional<Film> updated = filmStorage.getFilmById(saved.getId());
        assertThat(updated)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f.getName()).isEqualTo("New Title")
                );
    }
}

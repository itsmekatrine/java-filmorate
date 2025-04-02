package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
@Qualifier("genreDbStorage")
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbc;

    public GenreDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Genre> findAll() {
        String sql = "SELECT * FROM genres";
        return jdbc.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("id"), rs.getString("name")));
    }

    @Override
    public Optional<Genre> findById(int id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        List<Genre> result = jdbc.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("id"), rs.getString("name")), id);
        return result.stream().findFirst();
    }

    @Override
    public Set<Genre> getByIds(Set<Integer> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptySet();

        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT * FROM genres WHERE id IN (" + inSql + ")";

        return new HashSet<>(jdbc.query(sql, ids.toArray(), (rs, rowNum) -> new Genre(
                rs.getInt("id"),
                rs.getString("name")
        )));
    }
}

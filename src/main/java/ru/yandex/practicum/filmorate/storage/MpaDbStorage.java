package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbc;

    public MpaDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<MPARating> findAll() {
        String sql = "SELECT * FROM MPA";
        return jdbc.query(sql, (rs, rowNum) ->
                new MPARating(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")));
    }

    @Override
    public Optional<MPARating> findById(int id) {
        String sql = "SELECT * FROM MPA WHERE id = ?";
        List<MPARating> result = jdbc.query(sql, (rs, rowNum) ->
                new MPARating(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")), id);
        return result.stream().findFirst();
    }
}

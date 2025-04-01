package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbc.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return jdbc.query("SELECT * FROM users", mapper);
    }

    @Override
    public Optional<User> getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, mapper, userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void addFriend(int userId, int friendId) {
        String check = "SELECT status FROM friendships WHERE user_id = ? AND friend_id = ?";
        List<String> result = jdbc.query(check, (rs, rowNum) -> rs.getString("status"), friendId, userId);

        if (!result.isEmpty() && result.get(0).equals("PENDING")) {
            String update = "UPDATE friendships SET status = 'CONFIRMED' WHERE user_id = ? AND friend_id = ?";
            jdbc.update(update, friendId, userId);

            String insertBack = "INSERT INTO friendships(user_id, friend_id, status) VALUES (?, ?, 'CONFIRMED')";
            jdbc.update(insertBack, userId, friendId);
        } else {
            String sql = "INSERT INTO friendships(user_id, friend_id, status) VALUES (?, ?, 'PENDING')";
            jdbc.update(sql, userId, friendId);
        }
    }
}

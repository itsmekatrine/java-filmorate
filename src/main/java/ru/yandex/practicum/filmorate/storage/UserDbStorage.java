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
        String check = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbc.queryForObject(check, Integer.class, userId, friendId);

        if (count == 0) {
            jdbc.update("INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)", userId, friendId);
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        String sql = """
        SELECT u.*
        FROM users u
        JOIN friendships f ON u.id = f.friend_id
        WHERE f.user_id = ?
    """;
        return jdbc.query(sql, mapper, userId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        jdbc.update("DELETE FROM friendships WHERE user_id = ? AND friend_id = ?", userId, friendId);
    }

    @Override
    public Set<User> getCommonFriends(int userId, int otherId) {
        String sql = """
        SELECT u.*
        FROM users u
        JOIN friendships f1 ON u.id = f1.friend_id
        JOIN friendships f2 ON u.id = f2.friend_id
        WHERE f1.user_id = ? AND f2.user_id = ?
    """;

        return new HashSet<>(jdbc.query(sql, mapper, userId, otherId));
    }
}

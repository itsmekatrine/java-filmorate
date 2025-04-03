package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (userStorage.getUserById(user.getId()).isEmpty()) {
            throw new NoSuchElementException("Пользователь с ID " + user.getId() + " не найден");
        }
        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID " + userId + " не найден"));

        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NoSuchElementException("Друг с ID " + friendId + " не найден"));

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("User with id " + friendId + " not found"));

        userStorage.removeFriend(userId, friendId);
    }

    public Set<User> getCommonFriends(int userId, int otherId) {
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        userStorage.getUserById(otherId)
                .orElseThrow(() -> new NotFoundException("User with id " + otherId + " not found"));

        return userStorage.getCommonFriends(userId, otherId);
    }

    public User getUserById(int userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID " + userId + " не найден"));
    }

    public Set<User> getUserFriends(int userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        return new HashSet<>(userStorage.getFriends(userId));
    }
}

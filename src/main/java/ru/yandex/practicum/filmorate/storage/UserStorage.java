package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    Collection<User> getAllUsers();

    Optional<User> getUserById(int userId);

    void addFriend(int userId, int friendId);
}

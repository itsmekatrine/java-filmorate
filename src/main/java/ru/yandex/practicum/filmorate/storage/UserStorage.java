package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    Collection<User> getAllUsers();

    Optional<User> getUserById(int userId);

    void addFriend(int userId, int friendId);

    List<User> getFriends(int userId);

    void removeFriend(int userId, int friendId);

    Set<User> getCommonFriends(int userId1, int userId2);
}

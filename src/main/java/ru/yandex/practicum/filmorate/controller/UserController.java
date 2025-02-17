package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int userId = 1;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(userId++);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("Пользователь с таким ID не найден");
        }
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }
}


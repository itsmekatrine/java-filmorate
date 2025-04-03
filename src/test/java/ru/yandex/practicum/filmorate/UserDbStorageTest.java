package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    public void testGetUserById() {

        Optional<User> userOptional = userStorage.getUserById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testGetAllUsers() {
        Collection<User> users = userStorage.getAllUsers();

        assertThat(users).isNotEmpty();
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setEmail("new@example.com");
        user.setLogin("newlogin");
        user.setName("New User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userStorage.createUser(user);

        assertThat(created.getId()).isNotNull();

        Optional<User> found = userStorage.getUserById(created.getId());
        assertThat(found).isPresent();
    }

    @Test
    @Sql(scripts = "/clear-db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testUpdateUser() {
        User user = new User();
        user.setEmail("update@example.com");
        user.setLogin("updlogin");
        user.setName("Upd User");
        user.setBirthday(LocalDate.of(1992, 2, 2));
        User saved = userStorage.createUser(user);

        saved.setName("Updated Name");
        userStorage.updateUser(saved);

        Optional<User> updated = userStorage.getUserById(saved.getId());
        assertThat(updated)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u.getName()).isEqualTo("Updated Name")
                );
    }
}

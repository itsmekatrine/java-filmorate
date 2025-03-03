package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validator;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class ValidationTest {
	private Validator validator;

	@BeforeEach
	void setup() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void testValidFilm() {
		Film film = new Film();
		film.setName("The Lion King");
		film.setDescription("American animated musical coming-of-age drama film");
		film.setReleaseDate(LocalDate.of(1994, 6, 24));
		film.setDuration(88);

		Set<?> validationFields = validator.validate(film);
		assertTrue(validationFields.isEmpty(), "Фильм не должен иметь ошибок валидации");
	}

	@Test
	void testInvalidFilm() {
		Film film = new Film();
		film.setName("");
		film.setDescription("American animated musical coming-of-age drama film");
		film.setReleaseDate(LocalDate.of(2100, 1, 1));
		film.setDuration(-5);

		Set<?> validationFields = validator.validate(film);
		assertFalse(validationFields.isEmpty(), "Фильм должен иметь ошибки валидации");
	}

	@Test
	void testValidUser() {
		User user = new User();
		user.setEmail("test@gmail.com");
		user.setLogin("user123");
		user.setName("User");
		user.setBirthday(LocalDate.of(1990, 5, 20));

		Set<?> validationFields = validator.validate(user);
		assertTrue(validationFields.isEmpty(), "Пользователь не должен иметь ошибок валидации");
	}

	@Test
	void testInvalidUser() {
		User user = new User();
		user.setEmail("test-email");
		user.setLogin("user login");
		user.setName("");
		user.setBirthday(LocalDate.of(2100, 1, 1));

		Set<?> validationFields = validator.validate(user);
		assertFalse(validationFields.isEmpty(), "Пользователь должен иметь ошибки валидации");
	}
}

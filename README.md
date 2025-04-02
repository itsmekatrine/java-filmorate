# java-filmorate
Template repository for Filmorate project.

## Схема базы данных

Ниже представлена схема базы данных:

![Схема БД](https://github.com/itsmekatrine/java-filmorate/blob/add-friends-likes/src/main/java/ru/yandex/practicum/filmorate/resources/database_schema.jpeg#:~:text=database_schema.jpeg)

### 📌 Пояснение к схеме

Приложение включает следующие основные таблицы:

- `users` — хранит пользователей
- `films` — хранит фильмы
- `genres`, `film_genres` — хранит жанры и связи фильмов с жанрами
- `film_likes` — хранит лайки от пользователей к фильмам
- `mpa` — хранит рейтинги фильмов
- `friendships` — хранит связи дружбы между пользователями

---

### Примеры SQL-запросов

#### Добавить пользователя:
INSERT INTO users (email, login, name, birthday)
VALUES ('test@example.com', 'testuser', 'Test User', '1990-01-01');

#### Добавить фильм:
INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('Inception', 'Thriller', '2010-07-16', 148, 1);

#### Лайкнуть фильм:
INSERT INTO film_likes (film_id, user_id)
VALUES (1, 3);

####  Добавить в друзья:
INSERT INTO friendships (user_id, friend_id, status)
VALUES (1, 2, 'PENDING');

####  Удалить друга:
DELETE FROM friendships
WHERE user_id = 1 AND friend_id = 2;

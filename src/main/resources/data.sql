DELETE FROM users;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;

INSERT INTO users (email, login, name, birthday)
VALUES ('test@mail.com', 'testuser', 'Test User', '2000-01-01');

DELETE FROM mpa;
INSERT INTO mpa (id, rating, description) VALUES
(1, 'G', 'У фильма нет возрастных ограничений'),
(2, 'PG', 'Детям рекомендуется смотреть фильм с родителями'),
(3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
(4, 'R', 'Лицам до 17 лет просмотр можно только с родителями'),
(5, 'NC-17', 'Лицам до 18 лет просмотр запрещён');

DELETE FROM films;
ALTER TABLE films ALTER COLUMN id RESTART WITH 1;

INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('Inception', 'Mind-bending thriller', '2010-07-16', 148, 1);
INSERT INTO users (id, email, login, name, birthday)
VALUES (1, 'test@mail.com', 'testuser', 'Test User', '2000-01-01');

INSERT INTO mpa (id, rating, description) VALUES
(1, 'G', 'У фильма нет возрастных ограничений'),
(2, 'PG', 'Детям рекомендуется смотреть фильм с родителями'),
(3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
(4, 'R', 'Лицам до 17 лет просмотр можно только с родителями'),
(5, 'NC-17', 'Лицам до 18 лет просмотр запрещён');

INSERT INTO films (id, name, description, release_date, duration, mpa_id)
VALUES (1, 'Inception', 'Mind-bending thriller', '2010-07-16', 148, 1);

ALTER TABLE films ALTER COLUMN id RESTART WITH 2;
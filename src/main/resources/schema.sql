-- Таблица рейтинга
CREATE TABLE IF NOT EXISTS mpa (
    id INT PRIMARY KEY,
    rating VARCHAR(10) NOT NULL,
    description VARCHAR(200) NOT NULL
);

-- Таблица фильмов
CREATE TABLE IF NOT EXISTS films (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    release_date DATE,
    duration INT,
    mpa_id INT,
    FOREIGN KEY (mpa_id) REFERENCES mpa(id)
);

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(200) NOT NULL,
    login VARCHAR(100) NOT NULL,
    name VARCHAR(200),
    birthday DATE
);

-- Таблица жанров
CREATE TABLE IF NOT EXISTS genres (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Таблица фильмов и жанров
CREATE TABLE IF NOT EXISTS film_genres (
    film_id INT,
    genre_id INT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (genre_id) REFERENCES genres(id)
);

-- Таблица лайков
CREATE TABLE IF NOT EXISTS film_likes (
    film_id INT NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Таблица друзей пользователя
CREATE TABLE IF NOT EXISTS friendships (
    user_id INT,
    friend_id INT,
    status VARCHAR(20) DEFAULT 'PENDING',
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES users(id)
);
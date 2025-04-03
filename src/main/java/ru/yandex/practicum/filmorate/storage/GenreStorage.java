package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;

import java.util.Optional;
import java.util.Set;

public interface GenreStorage {

    List<Genre> findAll();

    Set<Genre> getByIds(Set<Integer> ids);

    Optional<Genre> findById(int id);
}

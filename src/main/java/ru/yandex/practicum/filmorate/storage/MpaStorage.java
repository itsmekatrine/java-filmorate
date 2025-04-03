package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    List<MPARating> findAll();

    Optional<MPARating> findById(int id);
}

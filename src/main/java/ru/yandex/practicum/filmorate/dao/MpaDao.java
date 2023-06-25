package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaDao {

    List<Mpa> getAll();

    Mpa getById(int ratingId);
}

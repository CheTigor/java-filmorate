package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {

    Genre create(Genre genre);

    List<Genre> getAll();

    Genre getById(int genreId);

    Genre put(Genre genre);

    Genre deleteById(int genreId);
}

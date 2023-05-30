package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getAll();

    Film getFilmById(int id);

    Film put(Film film);

    Film create(Film film);

    Film deleteFilmById(int filmId);

    Film addLike(int filmId, int userId);

    Film deleteLike(int filmId, int userId);
}

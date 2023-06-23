package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {

    List<Film> getAll();

    Film getFilmById(int id);

    Film put(Film film);

    Film create(Film film);

    Film deleteFilmById(int filmId);

    Film addLike(int filmId, int userId);

    Film deleteLike(int filmId, int userId);

    List<Film> getPopularFilms(int count);

    Film addGenre(int filmId, int genreId);

    Film removeGenre(int filmId, int genreId);

    List<Integer> getFilmLikesIds(int filmId);

    List<Integer> getFilmGenreIds(int filmId);
}

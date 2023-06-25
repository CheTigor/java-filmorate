package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {

    List<Film> getAll();

    Film getFilmById(int id);

    Film put(Film film);

    Film create(Film film);

    void deleteFilmById(int filmId);

    List<Film> getPopularFilms(int count);

    void addGenre(int filmId, int genreId);

    void removeGenre(int filmId, int genreId);

    List<Integer> getFilmLikesIds(int filmId);

    List<Integer> getFilmGenreIds(int filmId);

    void loadGenre(List<Film> films);
}

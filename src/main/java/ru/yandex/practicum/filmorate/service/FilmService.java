package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmDao filmDao;

    @Autowired
    public FilmService(FilmDao filmDao) {
        this.filmDao = filmDao;
    }

    public Film getFilmById(int id) {
        return filmDao.getFilmById(id);
    }

    public List<Film> getAll() {
        return filmDao.getAll();
    }

    public Film put(Film film) {
        filmValidate(film);
        return filmDao.put(film);
    }

    public Film create(Film film) {
        filmValidate(film);
        return filmDao.create(film);
    }

    public Film addLike(int filmId, int userId) {
        return filmDao.addLike(filmId, userId);
    }

    public Film deleteLike(int filmId, int userId) {
        return filmDao.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmDao.getPopularFilms(count);
    }

    private void filmValidate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Ошибка валидации - Неверные входные данные у film: " + film);
        }
    }
}



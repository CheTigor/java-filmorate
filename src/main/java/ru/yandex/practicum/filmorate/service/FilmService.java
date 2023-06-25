package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmDao filmDao;
    private final UserDao userDao;
    private final LikesDao likesDao;

    @Autowired
    public FilmService(FilmDao filmDao, UserDao userDao, LikesDao likesDao) {
        this.filmDao = filmDao;
        this.userDao = userDao;
        this.likesDao = likesDao;
    }

    public Film getFilmById(int id) {
        Film film = filmDao.getFilmById(id);
        filmDao.loadGenre(List.of(film));
        return film;
    }

    public List<Film> getAll() {
        final List<Film> films = filmDao.getAll();
        filmDao.loadGenre(films);
        return films;
    }

    public Film put(Film film) {
        filmValidate(film);
        final Film newFilm = filmDao.put(film);
        filmDao.loadGenre(List.of(newFilm));
        return newFilm;
    }

    public Film create(Film film) {
        filmValidate(film);
        final Film newFilm = filmDao.create(film);
        filmDao.loadGenre(List.of(newFilm));
        return newFilm;
    }

    public Film deleteFilmById(int filmId) {
        final Film film = filmDao.getFilmById(filmId);
        filmDao.deleteFilmById(filmId);
        filmDao.loadGenre(List.of(film));
        return film;
    }

    public Film addLike(int filmId, int userId) {
        final Film film = filmDao.getFilmById(filmId);
        filmDao.loadGenre(List.of(film));
        final User user = userDao.getUserById(userId);
        List<Integer> likesIds = filmDao.getFilmLikesIds(filmId);
        if (likesIds.contains(userId)) {
            log.debug("У фильма с id: {} уже есть лайк пользователя с id: {}", filmId, userId);
            return film;
        }
        likesDao.addLike(filmId, userId);
        film.setRate(film.getRate() + 1);
        filmDao.put(film);
        log.debug("Фильму с id: {} поставил лайк пользователь с id: {}", filmId, userId);
        return film;
    }

    public Film deleteLike(int filmId, int userId) {
        final Film film = filmDao.getFilmById(filmId);
        filmDao.loadGenre(List.of(film));
        final User user = userDao.getUserById(userId);
        likesDao.deleteLike(filmId, userId);
        film.setRate(film.getRate() - 1);
        filmDao.put(film);
        log.debug("user_id = {} убрал лайк фильму film_id = {}", userId, filmId);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        final List<Film> films = filmDao.getPopularFilms(count);
        filmDao.loadGenre(films);
        return films;
    }

    private void filmValidate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Ошибка валидации - Неверные входные данные у film: " + film);
        }
    }
}



package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.LikeNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film put(Film film) {
        return filmStorage.put(film);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film addLike(int filmId, int userId) {
        final Film film = filmStorage.getFilmById(filmId);
        if (film.getUserLikes().contains(userId)) {
            throw new AlreadyExistException(String.format(
                    "Пользователь c id: %d уже поставил лайк фильму c id: %d", userId, filmId));
        }
        film.getUserLikes().add(userId);
        log.debug("Фильму с id: {}, поставлен лайк от пользователя с id: {}", filmId, userId);
        return film;
    }

    public Film deleteLike(int filmId, int userId) {
        final Film film = filmStorage.getFilmById(filmId);
        if (!film.getUserLikes().contains(userId)) {
            throw new LikeNotFoundException(String.format(
                    "Пользователь с id: %d не ставил лайк фильму с id: %d", userId, filmId));
        }
        film.getUserLikes().remove(userId);
        log.debug("Фильму с id: {}, убрали лайк от пользователя с id: {}", filmId, userId);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        if (filmStorage.getAll() == null) {
            throw new NullPointerException("В базе данных нет ни одного фильма");
        }
        return filmStorage.getAll().stream()
                .sorted((fl1, fl2) -> fl2.getUserLikes().size() - fl1.getUserLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}



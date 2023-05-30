package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film put(Film film) {
        filmValidate(film);
        return filmStorage.put(film);
    }

    public Film create(Film film) {
        filmValidate(film);
        return filmStorage.create(film);
    }

    public Film addLike(int filmId, int userId) {
        //Проверка на null
        userStorage.getUserById(userId);
        return filmStorage.addLike(filmId, userId);
    }

    public Film deleteLike(int filmId, int userId) {
        //Проверка на null
        userStorage.getUserById(userId);
        return filmStorage.deleteLike(filmId, userId);
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

    private void filmValidate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Ошибка валидации - Неверные входные данные у film: " + film);
        }
    }
}



package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.*;

@Slf4j
@RequestMapping("/films")
@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping(value = "/{id}")
    public Film getFilmById(
            @PathVariable("id") @Min(1) int id
    ) {
        log.debug("Получен запрос (getFilmById) GET");
        final Film film = filmService.getFilmById(id);
        log.debug("Получен ответ (getFilmById) GET film: {}", film);
        return film;
    }

    @GetMapping
    public List<Film> getAll() {
        log.debug("Получен запрос (getAll) GET");
        final List<Film> films = filmService.getAll();
        log.debug("Получен ответ (getAll) GET films: {}", films);
        return films;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        log.debug("Получен запрос (put) PUT film: {}", film);
        final Film updateFilm = filmService.put(film);
        log.debug("Получен ответ (put) PUT updateFilm: {}", updateFilm);
        return updateFilm;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Получен запрос (create) POST film: {}", film);
        final Film newFilm = filmService.create(film);
        log.debug("Получен ответ (put) PUT newFilm: {}", newFilm);
        return newFilm;
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public Film addLike(
            @PathVariable("id") @Min(1) int id,
            @PathVariable("userId") @Min(1) int userId
    ) {
        log.debug("Получен запрос PUT, filmId: {}, userId: {}", id, userId);
        final Film film = filmService.addLike(id, userId);
        log.debug("Получен ответ PUT, film: {}", film);
        return film;
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public Film removeLike(
            @PathVariable("id") @Min(1) int id,
            @PathVariable("userId") @Min(1) int userId
    ) {
        log.debug("Получен запрос DELETE, filmId: {}, userId: {}", id, userId);
        final Film film = filmService.deleteLike(id, userId);
        log.debug("Получен ответ DELETE, film: {}", film);
        return film;
    }

    @GetMapping(value = "/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10", required = false) @Min(1) int count
    ) {
        log.debug("Получен запрос (getPopularFilms) GET");
        List<Film> films = filmService.getPopularFilms(count);
        log.debug("Получен ответ (getPopularFilms) GET films: {}", films);
        return films;
    }
}

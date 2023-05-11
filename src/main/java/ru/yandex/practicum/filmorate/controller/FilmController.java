package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequestMapping("/films")
@RestController
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @GetMapping
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        filmValidate(film);
        if (!films.containsKey(film.getId())) {
            for (Film anotherFilm : films.values()) {
                if (anotherFilm.equals(film)) {
                    throw new AlreadyExistException("Такой фильм уже есть в списке с другим id!");
                }
            }
        }
        films.put(film.getId(), film);
        log.info("Добавление/обновление фильма");
        return film;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        filmValidate(film);
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Создание нового фильма");
        return film;
    }

    private void filmValidate(@Valid Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Неверные входные данные");
        }
    }
}

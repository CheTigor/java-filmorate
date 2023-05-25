package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int storageId = 1;

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int id) {
        final Film film = films.get(id);
        if (film == null) {
            throw new FilmNotFoundException(String.format("Ошибка поиска фильма - не найден id: %d", id));
        }
        return film;
    }

    @Override
    public Film put(Film film) {
        filmValidate(film);
        if (film.getId() == 0) {
            throw new ValidationException("Ошибка обновления фильма, не заполнено поле id");
        }
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException(String.format("Ошибка обновления фильма, id: %d не существует", film.getId()));
        }
        films.put(film.getId(), film);
        log.debug("В базе обновлен film: {}", film);
        return film;
    }

    @Override
    public Film create(Film film) {
        filmValidate(film);
        if (film.getId() != 0) {
            throw new ValidationException("Ошибка создания фильма, заполнено поле id");
        }
        log.debug("Присвоение film id: {}", storageId);
        film.setId(storageId++);
        films.put(film.getId(), film);
        log.debug("В базу записан film: {}", film);
        return film;
    }

    private void filmValidate(@Valid Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Ошибка валидации - Неверные входные данные у film: " + film);
        }
    }
}

package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RequestMapping("/genres")
@RestController
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping()
    public List<Genre> getAll() {
        log.debug("Получен запрос (getAll) GET genres");
        final List<Genre> genres = genreService.getAll();
        log.debug("Получен ответ (getAll) GET genres: {}", genres);
        return genres;
    }

    @GetMapping(value = "/{id}")
    public Genre getById(@PathVariable("id") @Min(1) int id) {
        log.debug("Получен запрос (getById) GET genre");
        final Genre genre = genreService.getById(id);
        log.debug("Получен ответ (getById) GET genre: {}", genre);
        return genre;
    }
}

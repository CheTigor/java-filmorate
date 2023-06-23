package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RequestMapping("/mpa")
@RestController
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping()
    public List<Rating> getAll() {
        log.debug("Получен запрос (getAll) GET ratings");
        final List<Rating> ratings = ratingService.getAll();
        log.debug("Получен ответ (getAll) GET ratings: {}", ratings);
        return ratings;
    }

    @GetMapping(value = "/{id}")
    public Rating getById(@PathVariable("id") @Min(1) int id) {
        log.debug("Получен запрос (getById) GET rating");
        final Rating rating = ratingService.getById(id);
        log.debug("Получен ответ (getById) GET rating: {}", rating);
        return rating;
    }
}

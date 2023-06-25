package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RequestMapping("/mpa")
@RestController
public class MpaController {

    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping()
    public List<Mpa> getAll() {
        log.debug("Получен запрос (getAll) GET ratings");
        final List<Mpa> mpas = mpaService.getAll();
        log.debug("Получен ответ (getAll) GET ratings: {}", mpas);
        return mpas;
    }

    @GetMapping(value = "/{id}")
    public Mpa getById(@PathVariable("id") @Min(1) int id) {
        log.debug("Получен запрос (getById) GET rating");
        final Mpa mpa = mpaService.getById(id);
        log.debug("Получен ответ (getById) GET rating: {}", mpa);
        return mpa;
    }
}

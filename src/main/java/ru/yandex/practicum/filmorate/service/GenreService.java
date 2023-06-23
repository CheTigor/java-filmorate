package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Service
public class GenreService {

    private final GenreDao genreDao;

    @Autowired
    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Genre create(Genre genre) {
        return genreDao.create(genre);
    }

    public List<Genre> getAll() {
        return genreDao.getAll();
    }

    public Genre getById(int genreId) {
        return genreDao.getById(genreId);
    }

    public Genre put(Genre genre) {
        return genreDao.put(genre);
    }

    public Genre deleteById(int genreId) {
        return genreDao.deleteById(genreId);
    }
}

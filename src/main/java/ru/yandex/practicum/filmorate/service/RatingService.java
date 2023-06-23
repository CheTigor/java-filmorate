package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@Slf4j
@Service
public class RatingService {

    private final RatingDao ratingDao;

    @Autowired
    public RatingService(RatingDao ratingDao) {
        this.ratingDao = ratingDao;
    }

    public Rating create(Rating rating) {
        return ratingDao.create(rating);
    }

    public List<Rating> getAll() {
        return ratingDao.getAll();
    }

    public Rating getById(int ratingId) {
        return ratingDao.getById(ratingId);
    }

    public Rating put(Rating rating) {
        return ratingDao.put(rating);
    }

    public Rating deleteById(int ratingId) {
        return ratingDao.deleteById(ratingId);
    }
}

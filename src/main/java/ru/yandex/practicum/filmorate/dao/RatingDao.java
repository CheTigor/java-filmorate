package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingDao {

    Rating create(Rating rating);

    List<Rating> getAll();

    Rating getById(int ratingId);

    Rating put(Rating rating);

    Rating deleteById(int ratingId);
}

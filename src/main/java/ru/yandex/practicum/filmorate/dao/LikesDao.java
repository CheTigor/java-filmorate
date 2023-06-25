package ru.yandex.practicum.filmorate.dao;

public interface LikesDao {

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);
}

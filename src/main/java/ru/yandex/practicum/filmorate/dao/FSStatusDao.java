package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.FsStatus;

import java.util.List;

public interface FSStatusDao {

    List<FsStatus> getAll();

    FsStatus getById(int statusId);
}

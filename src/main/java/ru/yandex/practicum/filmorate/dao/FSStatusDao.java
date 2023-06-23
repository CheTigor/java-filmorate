package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.FsStatus;

import java.util.List;

public interface FSStatusDao {

    FsStatus create(FsStatus fSStatus);

    List<FsStatus> getAll();

    FsStatus getById(int statusId);

    FsStatus put(FsStatus fSStatus);

    FsStatus deleteById(int fSStatusId);
}

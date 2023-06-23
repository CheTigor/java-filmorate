package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FSStatusDao;
import ru.yandex.practicum.filmorate.model.FsStatus;

import java.util.List;

@Slf4j
@Service
public class FSStatusService {

    private final FSStatusDao fsStatusDao;

    @Autowired
    public FSStatusService(FSStatusDao fsStatusDao) {
        this.fsStatusDao = fsStatusDao;
    }

    public FsStatus create(FsStatus fSStatus) {
        return fsStatusDao.create(fSStatus);
    }

    public List<FsStatus> getAll() {
        return fsStatusDao.getAll();
    }

    public FsStatus getById(int fSStatusId) {
        return fsStatusDao.getById(fSStatusId);
    }

    public FsStatus put(FsStatus fSStatus) {
        return fsStatusDao.put(fSStatus);
    }

    public FsStatus deleteById(int fSStatusId) {
        return fsStatusDao.deleteById(fSStatusId);
    }
}

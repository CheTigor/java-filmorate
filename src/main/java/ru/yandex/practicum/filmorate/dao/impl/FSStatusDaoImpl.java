package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FSStatusDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FsStatus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class FSStatusDaoImpl implements FSStatusDao {

    private final JdbcTemplate jdbcTemplate;

    public FSStatusDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public FsStatus create(FsStatus fSStatus) {
        if (fSStatus.getId() != 0) {
            throw new ValidationException("Ошибка создания статуса дружбы, заполнено поле id");
        }
        String sqlQuery = "insert into \"friendship_status\"(\"status\") values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, fSStatus.getStatus());
            return stmt;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        String sqlQuery2 = "select * from \"friendship_status\" where \"id\" = ?";
        FsStatus fSStatusDB = jdbcTemplate.queryForObject(sqlQuery2, (rs, rowNum) -> makeFSStatus(rs), id);
        log.debug("В базе создан rating: {}", fSStatusDB);
        return fSStatusDB;
    }

    @Override
    public List<FsStatus> getAll() {
        String sql =  "select * from \"friendship_status\"";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFSStatus(rs));
    }

    @Override
    public FsStatus getById(int statusId) {
        String sql =  "select * from \"friendship_status\" where \"id\" = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFSStatus(rs), statusId);
    }

    @Override
    public FsStatus put(FsStatus fSStatus) {
        jdbcTemplate.update("update \"friendship_status\" set \"status\" = ? where \"id\" = ?",
                fSStatus.getStatus(), fSStatus.getId());
        String sql = "select * from \"friendship_status\" where \"id\" = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFSStatus(rs), fSStatus.getId());
    }

    @Override
    public FsStatus deleteById(int fSStatusId) {
        String sql = "select * from \"friendship_status\" where \"id\" = ?";
        FsStatus fSStatusDB = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFSStatus(rs), fSStatusId);
        jdbcTemplate.update("delete from \"friendship_status\" where \"id\" = ?", fSStatusId);
        return fSStatusDB;
    }

    private FsStatus makeFSStatus(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String status = rs.getString("status");
        return new FsStatus(id, status);
    }

}

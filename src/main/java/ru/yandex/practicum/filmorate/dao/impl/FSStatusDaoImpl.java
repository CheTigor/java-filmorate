package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FSStatusDao;
import ru.yandex.practicum.filmorate.model.FsStatus;

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
    public List<FsStatus> getAll() {
        String sql =  "SELECT * FROM friendship_status";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFSStatus(rs));
    }

    @Override
    public FsStatus getById(int statusId) {
        String sql =  "SELECT * FROM friendship_status WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFSStatus(rs), statusId);
    }

    private FsStatus makeFSStatus(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String status = rs.getString("status");
        return new FsStatus(id, status);
    }
}

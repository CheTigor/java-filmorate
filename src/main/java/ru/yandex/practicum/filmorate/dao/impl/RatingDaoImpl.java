package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class RatingDaoImpl implements RatingDao {

    private final JdbcTemplate jdbcTemplate;

    public RatingDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Rating create(Rating rating) {
        if (rating.getId() != 0) {
            throw new ValidationException("Ошибка создания рейтинга, заполнено поле id");
        }
        String sqlQuery = "insert into \"rating\"(\"name\") values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, rating.getName());
            return stmt;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        String sqlQuery2 = "select * from \"rating\" where \"id\" = ?";
        Rating ratingDB = jdbcTemplate.queryForObject(sqlQuery2, (rs, rowNum) -> makeRating(rs), id);
        log.debug("В базе создан rating: {}", ratingDB);
        return ratingDB;
    }

    @Override
    public List<Rating> getAll() {
        String sql =  "select * from \"rating\"";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeRating(rs));
    }

    @Override
    public Rating getById(int ratingId) {
        String sql =  "select * from \"rating\" where \"id\" = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeRating(rs), ratingId);
    }

    @Override
    public Rating put(Rating rating) {
        jdbcTemplate.update("update \"genre\" set \"name\" = ? where \"id\" = ?", rating.getName(), rating.getId());
        String sql = "select * from \"genre\" where \"id\" = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeRating(rs), rating.getId());
    }

    @Override
    public Rating deleteById(int ratingId) {
        String sql = "select * from \"rating\" where \"id\" = ?";
        Rating ratingDB = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeRating(rs), ratingId);
        jdbcTemplate.update("delete from \"rating\" where \"id\" = ?", ratingId);
        return ratingDB;
    }

    private Rating makeRating(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return new Rating(id, name);
    }
}

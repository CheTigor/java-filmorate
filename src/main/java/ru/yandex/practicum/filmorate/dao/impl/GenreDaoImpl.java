package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre create(Genre genre) {
        if (genre.getId() != 0) {
            throw new ValidationException("Ошибка создания жанра, заполнено поле id");
        }
        String sqlQuery = "insert into \"genre\"(\"name\") values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, genre.getName());
            return stmt;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        String sqlQuery2 = "select * from \"genre\" where \"id\" = ?";
        Genre genreDB = jdbcTemplate.queryForObject(sqlQuery2, (rs, rowNum) -> makeGenre(rs), id);
        log.debug("В базе создан genre: {}", genreDB);
        return genreDB;
    }

    @Override
    public List<Genre> getAll() {
        String sql =  "select * from \"genre\"";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Genre getById(int genreId) {
        String sql =  "select * from \"genre\" where \"id\" = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), genreId);
    }

    @Override
    public Genre put(Genre genre) {
        jdbcTemplate.update("update \"genre\" set \"name\" = ? where \"id\" = ?", genre.getName(), genre.getId());
        String sql = "select * from \"genre\" where \"id\" = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), genre.getId());
    }

    @Override
    public Genre deleteById(int genreId) {
        String sql = "select * from \"genre\" where \"id\" = ?";
        Genre genreDB = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), genreId);
        jdbcTemplate.update("delete from \"genre\" where \"id\" = ?", genreId);
        return genreDB;
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }
}

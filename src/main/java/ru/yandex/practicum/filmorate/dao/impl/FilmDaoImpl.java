package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Slf4j
@Component
public class FilmDaoImpl implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.*, m.* FROM films AS f INNER JOIN mpa AS m ON f.mpa_id = m.id";
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
        if (films.isEmpty()) {
            log.info("Таблица films пуста");
        } else {
            log.info("Получены все фильмы из таблицы films");
        }
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = "SELECT f.*, m.* FROM films AS f INNER JOIN mpa AS m ON f.mpa_id = m.id WHERE f.id = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeFilm(rs), id);
        if (film != null) {
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        } else {
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %s не найден.", id));
        }
    }

    @Override
    public Film put(Film film) {
        if (film.getId() == 0) {
            throw new ValidationException("Ошибка обновления пользователя login: "
                    + film.getName() + " - не заполнено поле id");
        }
        updateGenre(film);
        String sqlQuery = "UPDATE films SET title = ?, description = ?, " +
                "release_date = ?, duration = ?, rate = ?, mpa_id = ? WHERE id = ?";
        int count = jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getRate(), film.getMpa().getId(), film.getId());
        if (count == 0) {
            throw new FilmNotFoundException("Ошибка обновления фильма: id " + film.getId() + " нет в базе!");
        } else {
            final Film filmDB = getFilmById(film.getId());
            log.debug("В базе создан film: {}", filmDB);
            return filmDB;
        }
    }

    @Override
    public Film create(Film film) {
        //валидация
        if (film.getId() != 0) {
            throw new ValidationException("Ошибка создания фильма - заполнено поле id");
        }
        if (film.getMpa().getId() < 1 || film.getMpa().getId() > 5) {
            throw new ValidationException(String.format(
                    "mpa_id должен быть в пределах от 1 до 5, mpa_id: %d", film.getMpa().getId()));
        }
        //создание и получение id
        String sqlQuery = "INSERT INTO films (title, description, release_date, duration," +
                " rate, mpa_id) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        film.setId(id);
        updateGenre(film);
        final Film filmDB = getFilmById(id);
        log.debug("В базе создан film: {}", filmDB);
        return filmDB;
    }

    @Override
    public void deleteFilmById(int filmId) {
        jdbcTemplate.update("DELETE FROM films " +
                "WHERE id = ?", filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT f.*, m.* FROM films AS f INNER JOIN mpa AS m ON f.mpa_id = m.id " +
                        " ORDER BY rate DESC LIMIT ?", count);
        final List<Film> films = new ArrayList<>();
        while (filmRows.next()) {
            Film film = new Film(
                    filmRows.getInt("id"),
                    filmRows.getString("title"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getLong("duration"),
                    new Mpa(filmRows.getInt("mpa_id"), filmRows.getString("name")));
            film.setRate(filmRows.getInt("rate"));
            films.add(film);
        }
        return films;
    }

    public List<Integer> getFilmLikesIds(int filmId) {
        List<Integer> userIds = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT user_id FROM film_likes WHERE film_id = ?", filmId);
        while (filmRows.next()) {
            userIds.add(filmRows.getInt("user_id"));
        }
        return userIds;
    }

    public List<Integer> getFilmGenreIds(int filmId) {
        List<Integer> genreIds = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT genre_id FROM films_genre WHERE film_id = ?", filmId);
        while (filmRows.next()) {
            genreIds.add(filmRows.getInt("genre_id"));
        }
        return genreIds;
    }

    public void addGenre(int filmId, int genreId) {
        if (getFilmGenreIds(filmId).contains(genreId)) {
            return;
        }
        jdbcTemplate.update("INSERT INTO films_genre (film_id, genre_id) VALUES (?, ?)", filmId, genreId);
    }

    public void removeGenre(int filmId, int genreId) {
        jdbcTemplate.update("DELETE FROM films_genre WHERE film_id = ? AND genre_id = ?", filmId, genreId);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        long duration = rs.getLong("duration");
        int rate = rs.getInt("rate");
        Mpa mpa = new Mpa(rs.getInt("mpa_id"), rs.getString("name"));
        Film film = new Film(id, title, description, releaseDate, duration, mpa);
        film.setRate(rate);
        return film;
    }

    @Override
    public void loadGenre(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        final String sqlQuery = "select * from genre g, films_genre fg where fg.GENRE_ID = g.ID AND fg.FILM_ID in (" + inSql + ")";
        jdbcTemplate.query(sqlQuery, (rs) -> {
            final Film film = filmById.get(rs.getInt("FILM_ID"));
            film.addGenre(new Genre(rs.getInt("genre_id"), rs.getString("name")));
        }, films.stream().map(Film::getId).toArray());
    }

    private void updateGenre(Film film) {
        if (film.getGenres().isEmpty()) {
            jdbcTemplate.update("DELETE FROM films_genre WHERE film_id = ?", film.getId());
        } else {
            jdbcTemplate.update("DELETE FROM films_genre WHERE film_id = ?", film.getId());
            Set<Integer> genreIds = new HashSet<>();
            for (Genre genre : film.getGenres()) {
                genreIds.add(genre.getId());
            }
            for (Integer genreId : genreIds) {
                jdbcTemplate.update("INSERT INTO films_genre (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genreId);
            }
        }
    }
}

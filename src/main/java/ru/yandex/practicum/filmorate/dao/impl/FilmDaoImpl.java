package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class FilmDaoImpl implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "select * from \"films\"";
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
        String sqlQuery = "select * from \"films\" where \"id\" = ?";
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
        String sqlQuery = "update \"films\" set \"title\" = ?, \"description\" = ?, " +
                "\"release_date\" = ?, \"duration\" = ?, \"rate\" = ?, \"rating_id\" = ? where \"id\" = ?";
        int count = jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getRate(), film.getMpa().getId(), film.getId());
        if (count == 0) {
            throw new FilmNotFoundException("Ошибка обновления фильма: id " + film.getId() + " нет в базе!");
        } else {
            String sqlQuery2 = "select * from \"films\" where \"id\" = ?";
            Film filmDB = jdbcTemplate.queryForObject(sqlQuery2, (rs, rowNum) -> makeFilm(rs), film.getId());
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
                    "rating_id должен быть в пределах от 1 до 5, rating_id: %d", film.getMpa().getId()));
        }
        //создание и получение id
        String sqlQuery = "insert into \"films\" (\"title\", \"description\", \"release_date\", \"duration\"," +
                " \"rate\", \"rating_id\") values (?, ?, ?, ?, ?, ?)";
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
        String sqlQuery3 = "select * from \"films\" where \"id\" = ?";
        Film filmDB = jdbcTemplate.queryForObject(sqlQuery3, (rs, rowNum) -> makeFilm(rs), id);
        log.debug("В базе создан film: {}", filmDB);
        return filmDB;
    }

    @Override
    public Film deleteFilmById(int filmId) {
        final Film film = getFilmById(filmId);
        jdbcTemplate.update("delete from \"films\" " +
                "where \"id\" = ?", filmId);
        return film;
    }

    @Override
    public Film addLike(int filmId, int userId) {
        final Film film = getFilmById(filmId);
        film.setRate(film.getRate() + 1);
        String sqlQuery = "select * from \"users\" where \"id\" = ?";
        User user = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs), userId);
        if (user == null) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден.", userId));
        }
        List<Integer> likesIds = getFilmLikesIds(filmId);
        if (likesIds.contains(userId)) {
            log.debug("У фильма с id: {} уже есть лайк пользователя с id: {}", filmId, userId);
            return film;
        }
        jdbcTemplate.update("insert into \"film_likes\"(\"film_id\", \"user_id\") values (?, ?)", filmId, userId);
        put(film);
        log.debug("Фильму с id: {} поставил лайк пользователь с id: {}", filmId, userId);
        return film;
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        final Film film = getFilmById(filmId);
        film.setRate(film.getRate() - 1);
        String sqlQuery = "select * from \"users\" where \"id\" = ?";
        User user = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs), userId);
        if (user == null) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден.", userId));
        }
        jdbcTemplate.update("delete from \"film_likes\" " +
                "where \"film_id\" = ? and \"user_id\" = ?", filmId, userId);
        put(film);
        log.debug("user_id = {} убрал лайк фильму film_id = {}", userId, filmId);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "select * from \"films\" order by \"rate\" DESC LIMIT ?", count);
        final List<Film> films = new ArrayList<>();
        while (filmRows.next()) {
            Film film = new Film(
                    filmRows.getInt("id"),
                    filmRows.getString("title"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getLong("duration"),
                    giveRating(filmRows.getInt("rating_id")));
            film.setRate(filmRows.getInt("rate"));
            films.add(film);
        }
        return films;
    }

    public List<Integer> getFilmLikesIds(int filmId) {
        getFilmById(filmId);
        List<Integer> userIds = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "select \"user_id\" from \"film_likes\" where \"film_id\" = ?", filmId);
        while (filmRows.next()) {
            userIds.add(filmRows.getInt("user_id"));
        }
        return userIds;
    }

    public List<Integer> getFilmGenreIds(int filmId) {
        getFilmById(filmId);
        List<Integer> genreIds = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "select \"genre_id\" from \"films_genre\" where \"film_id\" = ?", filmId);
        while (filmRows.next()) {
            genreIds.add(filmRows.getInt("genre_id"));
        }
        return genreIds;
    }

    public Film addGenre(int filmId, @Min(1) @Max(6) int genreId) {
        Film film = getFilmById(filmId);
        if (getFilmGenreIds(filmId).contains(genreId)) {
            return film;
        }
        jdbcTemplate.update("insert into \"films_genre\" (\"film_id\", \"genre_id\") values (?, ?)",
                filmId, genreId);
        return film;
    }

    public Film removeGenre(int filmId, @Min(1) @Max(6) int genreId) {
        Film film = getFilmById(filmId);
        jdbcTemplate.update("delete from \"films_genre\" where \"film_id\" = ? and \"genre_id\" = ?",
                filmId, genreId);
        return film;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String login = rs.getString("login");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, name, email, login, birthday);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        long duration = rs.getLong("duration");
        int rate = rs.getInt("rate");
        Rating rating = giveRating(rs.getInt("rating_id"));

        Film film = new Film(id, title, description, releaseDate, duration, rating);
        film.setRate(rate);

        SqlRowSet sql = jdbcTemplate.queryForRowSet(
                "select \"genre_id\" from \"films_genre\" where \"film_id\" = ?", id);
        while (sql.next()) {
            film.getGenres().add(giveGenre((sql.getInt("genre_id"))));
        }

        return film;
    }

    private Rating giveRating(int ratingId) {
        SqlRowSet sql = jdbcTemplate.queryForRowSet("select \"name\" from \"rating\" where \"id\" = ?", ratingId);
        sql.next();
        return new Rating(ratingId, sql.getString("name"));
    }

    private Genre giveGenre(int genreId) {
        SqlRowSet sql = jdbcTemplate.queryForRowSet("select \"name\" from \"genre\" where \"id\" = ?", genreId);
        sql.next();
        return new Genre(genreId, sql.getString("name"));
    }

    private void updateGenre(Film film) {
        if (film.getGenres().isEmpty()) {
            jdbcTemplate.update("delete from \"films_genre\" where \"film_id\" = ?", film.getId());
        } else {
            jdbcTemplate.update("delete from \"films_genre\" where \"film_id\" = ?", film.getId());
            Set<Integer> genreIds = new HashSet<>();
            for (Genre genre : film.getGenres()) {
                genreIds.add(genre.getId());
            }
            for (Integer genreId : genreIds) {
                jdbcTemplate.update("insert into \"films_genre\" (\"film_id\", \"genre_id\") values (?, ?)",
                        film.getId(), genreId);
            }
        }
    }
}

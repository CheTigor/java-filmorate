package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));
        if (users.isEmpty()) {
            log.info("Таблица films пуста");
        } else {
            log.info("Получены все фильмы из таблицы films");
        }
        return users;
    }

    @Override
    public User getUserById(int id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        User user = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs), id);
        if (user != null) {
            log.info("Найден пользователь с id: {}", user.getId());
            return user;
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден.", id));
        }
    }

    @Override
    public User put(User user) {
        if (user.getId() == 0) {
            throw new ValidationException("Ошибка обновления пользователя login: "
                    + user.getLogin() + " - не заполнено поле id");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("name присваивает значение login, name = {}", user.getName());
        }
        String sqlQuery = "UPDATE users SET name = ?, email = ?, " +
                "login = ?, birthday = ? WHERE id = ?";
        int count = jdbcTemplate.update(sqlQuery, user.getName(), user.getEmail(),
                user.getLogin(), user.getBirthday(), user.getId());
        if (count == 0) {
            throw new UserNotFoundException("Ошибка обновления пользователя: id " + user.getId() + " нет в базе!");
        } else {
            String sqlQuery2 = "SELECT * FROM users WHERE id = ?";
            User userDB = jdbcTemplate.queryForObject(sqlQuery2, (rs, rowNum) -> makeUser(rs), user.getId());
            log.debug("В базе создан user: {}", userDB);
            return userDB;
        }
    }

    @Override
    public User create(User user) {
        //валидация
        if (user.getId() != 0) {
            throw new ValidationException("Ошибка создания пользователя - заполнено поле id");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("name присваивает значение login, name = {}", user.getName());
        }
        //создание и получение id
        String sqlQuery = "INSERT INTO users(name, email, login, birthday)" +
                " VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int count = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getLogin());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        //Проверка, что операция выполнилась
        if (count == 0) {
            throw new UserNotFoundException("Ошибка создания пользователя: name " + user.getName());
        } else {
            String sqlQuery2 = "SELECT * FROM users WHERE id = ?";
            User userDB = jdbcTemplate.queryForObject(sqlQuery2, (rs, rowNum) -> makeUser(rs), id);
            log.debug("В базе создан user: {}", userDB);
            return userDB;
        }
    }

    @Override
    public void deleteUserById(int userId) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
    }

    public List<Integer> getUserFriendsIds(int id) {
        List<Integer> userIds = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                "SELECT user_id_2 FROM friendship WHERE user_id_1 = ?", id);
        while (userRows.next()) {
            userIds.add(userRows.getInt("user_id_2"));
        }
        if (userIds.isEmpty()) {
            log.info("У пользователя id: {} нет друзей", id);
        } else {
            log.info("Получены все id друзей пользователя с id: {}", id);
        }
        return userIds;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String login = rs.getString("login");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, name, email, login, birthday);
    }
}

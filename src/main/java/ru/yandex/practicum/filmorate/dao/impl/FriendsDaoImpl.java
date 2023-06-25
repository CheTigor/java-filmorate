package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FriendsDao;

@Slf4j
@Component
public class FriendsDaoImpl implements FriendsDao {

    private final JdbcTemplate jdbcTemplate;

    public FriendsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    //statusId: 1 - подтвержденная, 2 - неподтвержденная
    public void addFriend(int id, int friendId) {
        int statusId = 2;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM friendship " +
                "WHERE user_id_2 = ? AND user_id_1 = ?", id, friendId);
        if (userRows.next()) {
            statusId = 1;
            String sqlQuery = "UPDATE friendship SET status = ? WHERE user_id_2 = ? AND user_id_1 = ?";
            jdbcTemplate.update(sqlQuery, statusId, id, friendId);
            log.debug("user_id_2 = {} и user_id_1 = {} поменялся статус дружбы на подтвержденный", id, friendId);
        }
        String sqlQuery = "INSERT INTO friendship (user_id_1, user_id_2, status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId, statusId);
        log.debug("user_id_1 = {} отправил запрос на дружбу user_id_2 = {}", id, friendId);
    }


    @Override
    public void removeFriend(int id, int friendId) {
        int statusId = 2;
        jdbcTemplate.update("DELETE FROM friendship " +
                "WHERE user_id_1 = ? AND user_id_2 = ?", id, friendId);
        log.debug("user_id_1 = {} удалил из друзей user_id_2 = {}", id, friendId);
        jdbcTemplate.update("UPDATE friendship SET status_id = ? " +
                "WHERE user_id_2 = ? AND user_id_1 = ?", statusId, id, friendId);
        log.debug("user_id_2 = {} и user_id_1 = {} поменялся статус дружбы на неподтвержденный", id, friendId);
    }

}

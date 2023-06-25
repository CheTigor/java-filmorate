package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {

    List<User> getAll();

    User getUserById(int id);

    User put(User user);

    User create(User user);

    void deleteUserById(int userId);

    List<Integer> getUserFriendsIds(int id);
}

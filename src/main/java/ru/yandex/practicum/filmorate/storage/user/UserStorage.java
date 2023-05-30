package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAll();

    User getUserById(int id);

    User put(User user);

    User create(User user);

    User addFriend(int id, int friendId);

    User removeFriend(int id, int friendId);

    User deleteUserById(int userId);
}

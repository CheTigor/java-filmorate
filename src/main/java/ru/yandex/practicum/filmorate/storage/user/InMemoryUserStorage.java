package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int storageId = 0;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        final User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException(String.format("Ошибка поиска пользователя - не найден id: %d", id));
        }
        return user;
    }

    @Override
    public User put(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Ошибка обновления пользователя: id " + user.getId() + " нет в базе!");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("name присваивает значение login, name = {}", user.getName());
        }
        users.put(user.getId(), user);
        log.debug("В базе обновлен user: {}", user);
        return user;
    }

    @Override
    public User create(User user) {
        if (user.getId() != 0) {
            throw new ValidationException("Ошибка создания пользователя - заполнено поле id");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("name присваивает значение login, name = {}", user.getName());
        }
        user.setId(++storageId);
        log.debug("Присвоение user id: {}", user.getId());
        users.put(user.getId(), user);
        log.debug("В базу данных сохранен user: {}", user);
        return user;
    }

    @Override
    public User addFriend(int id, int friendId) {
        final User user = getUserById(id);
        final User friend = getUserById(friendId);
        user.getFriendsId().add(friendId);
        friend.getFriendsId().add(id);
        log.debug("Пользователи: {}, {} - стали друзьями", user, friend);
        return user;
    }

    @Override
    public User removeFriend(int id, int friendId) {
        final User user = getUserById(id);
        final User friend = getUserById(friendId);
        user.getFriendsId().remove(friendId);
        friend.getFriendsId().remove(id);
        log.debug("Пользователи: {}, {} - перестали быть друзьями", user, friend);
        return user;
    }

    @Override
    public User deleteUserById(int userId) {
        final User user = users.remove(userId);
        if (user == null) {
            throw new ValidationException(String.format("Ошибка удаления фильма - фильм по id: %d не найден", userId));
        }
        return user;
    }
}

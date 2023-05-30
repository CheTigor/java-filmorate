package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User put(@Valid User user) {
        return userStorage.put(user);
    }

    public User create(@Valid User user) {
        return userStorage.create(user);
    }

    public User addFriend(int id, int friendId) {
        return userStorage.addFriend(id, friendId);
    }

    public User removeFriend(int id, int friendId) {
        return userStorage.removeFriend(id, friendId);
    }

    public List<User> getUserFriends(int id) {
        final User user = userStorage.getUserById(id);
        return userStorage.getAll().stream()
                .filter(x -> user.getFriendsId().contains(x.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int anotherId) {
        final User user = userStorage.getUserById(id);
        final User anotherUser = userStorage.getUserById(anotherId);
        Set<Integer> commonFriends = new HashSet<>(user.getFriendsId());
        commonFriends.retainAll(anotherUser.getFriendsId());
        return userStorage.getAll().stream()
                .filter(x -> commonFriends.contains(x.getId()))
                .collect(Collectors.toList());
    }
}

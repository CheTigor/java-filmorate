package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    public List<User> getAll() {
        return userDao.getAll();
    }

    public User put(@Valid User user) {
        return userDao.put(user);
    }

    public User create(@Valid User user) {
        return userDao.create(user);
    }

    public User addFriend(int id, int friendId) {
        return userDao.addFriend(id, friendId);
    }

    public User removeFriend(int id, int friendId) {
        return userDao.removeFriend(id, friendId);
    }

    public List<User> getUserFriends(int id) {
        List<Integer> friendsIds = userDao.getUserFriendsIds(id);
        List<User> friends = new ArrayList<>();
        for (Integer ids : friendsIds) {
            friends.add(userDao.getUserById(ids));
        }
        return friends;
    }

    public List<User> getCommonFriends(int id, int anotherId) {
        Set<User> commonFriends = new HashSet<>(getUserFriends(id));
        commonFriends.retainAll(getUserFriends(anotherId));
        return new ArrayList<>(commonFriends);
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendsDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final UserDao userDao;
    private final FriendsDao friendsDao;

    @Autowired
    public UserService(UserDao userDao, FriendsDao friendsDao) {
        this.userDao = userDao;
        this.friendsDao = friendsDao;
    }

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    public List<User> getAll() {
        return userDao.getAll();
    }

    public User put(User user) {
        return userDao.put(user);
    }

    public User create(User user) {
        return userDao.create(user);
    }

    public User deleteUserById(int id) {
        final User user = userDao.getUserById(id);
        userDao.deleteUserById(id);
        return user;
    }

    public User addFriend(int id, int friendId) {
        final User user = userDao.getUserById(id);
        final User friend = userDao.getUserById(friendId);
        List<Integer> friendsIds = userDao.getUserFriendsIds(id);
        if (friendsIds.contains(friendId)) {
            log.debug("user id = {} уже является другом user id = {}", id, friendId);
            return user;
        }
        friendsDao.addFriend(id, friendId);
        return user;
    }

    public User removeFriend(int id, int friendId) {
        final User user = userDao.getUserById(id);
        final User friend = userDao.getUserById(friendId);
        friendsDao.removeFriend(id, friendId);
        return user;
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

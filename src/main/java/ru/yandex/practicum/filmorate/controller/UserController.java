package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.*;

@Slf4j
@RequestMapping("/users")
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable("id") @Min(1) int id) {
        log.debug("Получен запрос (getUserById) GET id: {}", id);
        final User user = userService.getUserById(id);
        log.debug("Получен ответ (getUserById) GET user: {}", user);
        return user;
    }

    @GetMapping
    public List<User> getAll() {
        log.debug("Получен запрос (getAll) GET");
        final List<User> users = userService.getAll();
        log.debug("Получен ответ (getAll) GET users: {}", users);
        return users;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        log.debug("Получен запрос (put) PUT user: {}", user);
        final User updateUser = userService.put(user);
        log.debug("Получен ответ (create) POST updateUser: {}", updateUser);
        return updateUser;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Получен запрос (create) POST user: {}", user);
        final User newUser = userService.create(user);
        log.debug("Получен ответ (create) POST newUser: {}", newUser);
        return newUser;
    }

    @PutMapping(value = "{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") @Min(1) int id, @PathVariable("friendId") @Min(1) int friendId) {
        log.debug("Получен запрос PUT, userId: {}, friendId: {}", id, friendId);
        User user = userService.addFriend(id, friendId);
        log.debug("Получен ответ PUT, user: {}", user);
        return user;
    }

    @DeleteMapping(value = "{id}/friends/{friendId}")
    public User removeFriend(@PathVariable("id") @Min(1) int id, @PathVariable("friendId") @Min(1) int friendId) {
        log.debug("Получен запрос DELETE, userId: {}, friendId: {}", id, friendId);
        final User user = userService.removeFriend(id, friendId);
        log.debug("Получен ответ DELETE, user: {}", user);
        return user;
    }

    @GetMapping(value = "{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") @Min(1) int id) {
        log.debug("Получен запрос GET, userId: {}", id);
        List<User> friends = userService.getUserFriends(id);
        log.debug("Получен ответ GET, friends: {}", friends);
        return friends;
    }

    @GetMapping(value = "{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") @Min(1) int id,
                                       @PathVariable("otherId") @Min(1) int otherId) {
        log.debug("Получен запрос GET, userId: {}, friendId: {}", id, otherId);
        List<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.debug("Получен ответ GET, commonFriends: {}", commonFriends);
        return commonFriends;
    }
}

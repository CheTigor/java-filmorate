package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RequestMapping("/users")
@RestController
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        log.debug("Получен запрос PUT user, user: {}", user);
        if (!users.containsKey(user.getId())) {
            throw new NullPointerException("Ошибка обновления пользователя: id " + user.getId() + " нет в базе!");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя подставляется в логин, name = {}", user.getName());
        }
        users.put(user.getId(), user);
        log.debug("Получен ответ PUT user, user: {}", user);
        return user;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Получен запрос POST user, user: {}", user);
        for (User anotherUser : users.values()) {
            if (anotherUser.equals(user)) {
                throw new AlreadyExistException("Такой пользователь уже есть в списке!");
            }
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя подставляется в логин, name = {}", user.getName());
        }
        user.setId(id++);
        log.debug("Присвоение user id: {}", user.getId());
        users.put(user.getId(), user);
        log.debug("Получен ответ POST user, user: {}", user);
        return user;
    }
}

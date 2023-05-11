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
        if (!users.containsKey(user.getId()))
            for (User anotherUser : users.values()) {
                if (anotherUser.equals(user)) {
                    throw new AlreadyExistException("Такой пользователь уже есть в списке с другим id!");
                }
            }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавление/обновление пользователя: {}", user.getEmail());
        return user;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        for (User anotherUser : users.values()) {
            if (anotherUser.equals(user)) {
                throw new AlreadyExistException("Такой пользователь уже есть в списке!");
            }
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Создание нового пользователя: {}", user.getEmail());
        return user;
    }
}

package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private int id;
    @Email
    private final String email;
    @NotBlank
    private final String login;
    private String name;
    @NotNull
    @Past
    private final LocalDate birthday;
    private final Set<Integer> friendsId = new HashSet<>();
}

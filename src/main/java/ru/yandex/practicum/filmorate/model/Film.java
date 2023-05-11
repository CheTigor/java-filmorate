package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

@Data
public class Film {

    private int id;
    @NotBlank
    private final String name;
    @NotNull
    @Size(max = 200)
    private final String description;
    @NotNull
    private final LocalDate releaseDate;
    @NotNull
    @Positive
    private final long duration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return duration == film.duration && Objects.equals(name, film.name) && Objects.equals(description, film.description) && Objects.equals(releaseDate, film.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, releaseDate, duration);
    }
}

package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends NullPointerException {
    public FilmNotFoundException(String message) {
        super(message);
    }
}

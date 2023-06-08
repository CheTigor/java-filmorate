package ru.yandex.practicum.filmorate.exception;

public class LikeNotFoundException extends NullPointerException {

    public LikeNotFoundException(String message) {
        super(message);
    }
}

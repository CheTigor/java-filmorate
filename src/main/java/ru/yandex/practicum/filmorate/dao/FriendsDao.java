package ru.yandex.practicum.filmorate.dao;

public interface FriendsDao {

    void addFriend(int id, int friendId);

    void removeFriend(int id, int friendId);
}

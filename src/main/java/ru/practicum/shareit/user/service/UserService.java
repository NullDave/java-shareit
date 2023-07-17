package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User add(User user);

    User update(Long userId, User user);

    User get(Long UserId);

    void delete(Long UserId);

}

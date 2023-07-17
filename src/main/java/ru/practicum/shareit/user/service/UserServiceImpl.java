package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserStorage userStorage;


    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User add(User user) {
        return userStorage.add(user);
    }

    @Override
    public User update(Long userId, User user) {
        return userStorage.update(userId, user);
    }

    @Override
    public User get(Long userId) {
        return userStorage.get(userId);
    }

    @Override
    public void delete(Long userId) {
        userStorage.delete(userId);
    }
}

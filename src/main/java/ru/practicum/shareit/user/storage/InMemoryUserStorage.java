package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailBusyException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users;
    private final Set<String> emailUsers;
    private long countUser = 1;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User add(User user) {
        addEmail(user.getEmail());
        user.setId(countUser++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        User currentUser = get(userId);
        if (user.getName() != null && !user.getName().isBlank()) {
            currentUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            emailUsers.remove(currentUser.getEmail());
            addEmail(user.getEmail());
            currentUser.setEmail(user.getEmail());
        }
        return currentUser;
    }

    @Override
    public User get(Long userId) {
        User currentUser = users.get(userId);
        if (currentUser == null) throw new NotFoundException("пользователь не найден id:" + userId);
        return currentUser;
    }

    @Override
    public void delete(Long userId) {
        User currentUser = get(userId);
        emailUsers.remove(currentUser.getEmail());
        users.remove(currentUser.getId());
    }


    private void addEmail(String email) {
        if (!emailUsers.add(email))
            throw new EmailBusyException(email + " занять");
    }
}

package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailBusyException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User add(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new EmailBusyException(user.getEmail() + " занять другим пользователем");
        }
    }

    @Override
    @Transactional
    public User update(Long userId, User user) {
        User currentUser = getById(userId);
        if (user.getName() != null && !user.getName().isBlank()) {
            currentUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (userRepository.existsByEmailIgnoringUser(user.getEmail(), userId)) {
                throw new EmailBusyException(user.getEmail() + " занять другим пользователем");
            }
            currentUser.setEmail(user.getEmail());
        }
        return userRepository.save(currentUser);
    }

    @Override
    public User get(Long userId) {
        return getById(userId);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    private User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("пользователь не найден id:" + id));
    }
}

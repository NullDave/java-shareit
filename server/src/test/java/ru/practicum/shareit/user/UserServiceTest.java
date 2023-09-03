package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailBusyException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private static User user;
    private static User user2;
    private static List<User> userList;

    @BeforeAll
    public static void setup() {
        user = User.builder()
                .id(1L)
                .email("simple@id.ru")
                .name("Dave")
                .build();
        user2 = User.builder()
                .id(2L)
                .email("mail@id.ru")
                .name("Danil")
                .build();
        userList = List.of(user, user2);
    }

    @Test
    public void testGetAll() {
        when(userRepository.findAll())
                .thenReturn(userList);
        List<User> currentList = userService.getAll();
        assertEquals(currentList.size(), 2);
        assertEquals(currentList.get(0).getId(), user.getId());
        assertEquals(currentList.get(0).getName(), user.getName());
        assertEquals(currentList.get(1).getId(), user2.getId());
        assertEquals(currentList.get(1).getName(), user2.getName());
    }

    @Test
    public void testGet() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        User currentUser = userService.get(1L);
        assertEquals(currentUser.getId(), user.getId());
        assertEquals(currentUser.getName(), user.getName());
        assertEquals(currentUser.getEmail(), user.getEmail());
    }

    @Test
    public void testWithIncorrectUserId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.get(3L));
    }

    @Test
    public void testAdd() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        User currentUser = userService.add(user);
        assertEquals(currentUser.getId(), user.getId());
        assertEquals(currentUser.getName(), user.getName());
        assertEquals(currentUser.getEmail(), user.getEmail());
    }

    @Test
    public void testUpdate() {
        User updateUser = User.builder()
                .id(1L)
                .email("qwer@id.ru")
                .name("Dave")
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoringUser(anyString(), anyLong()))
                .thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenReturn(updateUser);
        User currentUser = userService.update(1L, user);
        assertEquals(currentUser.getId(), updateUser.getId());
        assertEquals(currentUser.getName(), updateUser.getName());
        assertEquals(currentUser.getEmail(), updateUser.getEmail());
    }

    @Test
    public void testUpdateWithBusyEmail() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoringUser(anyString(), anyLong()))
                .thenReturn(true);
        assertThrows(EmailBusyException.class, () -> userService.update(1L, user));
    }
}

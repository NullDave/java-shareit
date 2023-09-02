package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

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
    @SneakyThrows
    public void testGetAll() {
        when(userService.getAll())
                .thenReturn(userList);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(user.getId()))
                .andExpect(jsonPath("$[0].name").value(user.getName()))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()))
                .andExpect(jsonPath("$[1].id").value(user2.getId()))
                .andExpect(jsonPath("$[1].name").value(user2.getName()))
                .andExpect(jsonPath("$[1].email").value(user2.getEmail()));
        verify(userService, only())
                .getAll();
        verifyNoMoreInteractions(userService);
    }

    @Test
    @SneakyThrows
    public void testGet() {
        when(userService.get(anyLong()))
                .thenReturn(user);
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
        verify(userService, only())
                .get(anyLong());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @SneakyThrows
    public void testAdd() {
        when(userService.add(any(User.class)))
                .thenReturn(user);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserMapper.toUserDto(user))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
        verify(userService, only())
                .add(any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @SneakyThrows
    public void testAddWithBlankName() {
        User newUser = User.builder()
                .name("")
                .email("wow@bb.com")
                .build();
        when(userService.add(any(User.class)))
                .thenReturn(newUser);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserMapper.toUserDto(newUser))))
                .andExpect(status().isBadRequest());
        verify(userService, never())
                .add(any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @SneakyThrows
    public void testUpdate() {
        User updateUser = User.builder()
                .id(1L)
                .email("qwer@id.ru")
                .name("Dave")
                .build();
        when(userService.update(anyLong(), any(User.class)))
                .thenReturn(updateUser);
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserMapper.toUserDto(updateUser))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(updateUser.getName()))
                .andExpect(jsonPath("$.email").value(updateUser.getEmail()));
        verify(userService, only())
                .update(anyLong(), any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @SneakyThrows
    public void testDelete() {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}

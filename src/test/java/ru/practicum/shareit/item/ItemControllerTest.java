package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    private static User user;
    private static ItemDto item;
    private static ItemDto item2;
    private static Item item3;
    private static CommentDto comment;

    @BeforeAll
    public static void setup() {
        user = User.builder()
                .id(1L)
                .email("simple@id.ru")
                .name("Dave")
                .build();
        item = ItemDto.builder()
                .id(1L)
                .name("Ручка Erich Krause")
                .description("Выиграл в \"СВОЯ ИГРА\" инкрустирована бриллиантами.")
                .available(true)
                .build();
        comment = CommentDto.builder()
                .id(1L)
                .authorName(user.getName())
                .text("Лопата топ")
                .created(LocalDateTime.now())
                .build();
        item2 = ItemDto.builder()
                .id(2L)
                .name("Лопата")
                .description("Лопата Деда.")
                .comments(List.of(comment))
                .requestId(user.getId())
                .available(false)
                .build();
        item3 = Item.builder()
                .id(3L)
                .name("Дрель")
                .description("Ручная дрель")
                .available(true)
                .build();
    }

    @Test
    @SneakyThrows
    public void testGetAllByUser() {
        when(itemService.getAllByUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(item, item2));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(item.getId()))
                .andExpect(jsonPath("$[0].name").value(item.getName()))
                .andExpect(jsonPath("$[0].available").value(item.getAvailable()))
                .andExpect(jsonPath("$[1].id").value(item2.getId()))
                .andExpect(jsonPath("$[1].name").value(item2.getName()))
                .andExpect(jsonPath("$[1].comments[0].id").value(comment.getId()));
        verify(itemService, only())
                .getAllByUser(anyLong(), anyInt(), anyInt());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    @SneakyThrows
    public void testGet() {
        when(itemService.get(anyLong(), anyLong()))
                .thenReturn(item2);
        mockMvc.perform(get("/items/2")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item2.getId()))
                .andExpect(jsonPath("$.name").value(item2.getName()))
                .andExpect(jsonPath("$.available").value(item2.getAvailable()))
                .andExpect(jsonPath("$.comments[0].id").value(comment.getId()));
        verify(itemService, only())
                .get(anyLong(), anyLong());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    @SneakyThrows
    public void testSearch() {
        when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(item3));
        mockMvc.perform(get("/items/search")
                        .param("text", "дрель")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(item3.getId()))
                .andExpect(jsonPath("$[0].name").value(item3.getName()))
                .andExpect(jsonPath("$[0].available").value(item3.getAvailable()));
        verify(itemService, only())
                .search(anyString(), anyInt(), anyInt());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    @SneakyThrows
    public void testAdd() {
        Item newItem = Item.builder()
                .id(1L)
                .name("Палатка")
                .description("Палатка одноместная.")
                .available(true)
                .build();
        when(itemService.add(any(Item.class), anyLong()))
                .thenReturn(newItem);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newItem.getId()))
                .andExpect(jsonPath("$.name").value(newItem.getName()))
                .andExpect(jsonPath("$.available").value(newItem.getAvailable()));
        verify(itemService, only())
                .add(any(Item.class), anyLong());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    @SneakyThrows
    public void testAddWithBlankName() {
        Item newItem = Item.builder()
                .id(1L)
                .name("")
                .description("Палатка одноместная.")
                .available(true)
                .build();
        when(itemService.add(any(Item.class), anyLong()))
                .thenReturn(newItem);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isBadRequest());
        verify(itemService, never())
                .add(any(Item.class), anyLong());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    @SneakyThrows
    public void testUpdate() {
        Item newItem = Item.builder()
                .id(1L)
                .name("Палатка")
                .description("Палатка двухместная.")
                .available(true)
                .build();
        when(itemService.update(any(Item.class), anyLong()))
                .thenReturn(newItem);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newItem.getId()))
                .andExpect(jsonPath("$.name").value(newItem.getName()))
                .andExpect(jsonPath("$.description").value(newItem.getDescription()))
                .andExpect(jsonPath("$.available").value(newItem.getAvailable()));
        verify(itemService, only())
                .update(any(Item.class), anyLong());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    @SneakyThrows
    public void testAddComment() {
        Comment newComment = Comment.builder()
                .id(2L)
                .item(item3)
                .author(user)
                .created(LocalDateTime.now())
                .text("Хорошая дрель, руки накачал и дырку сделал.")
                .build();
        when(itemService.addComment(any(Comment.class), anyLong(), anyLong()))
                .thenReturn(newComment);
        mockMvc.perform(post("/items/3/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newComment.getId()))
                .andExpect(jsonPath("$.authorName").value(newComment.getAuthor().getName()))
                .andExpect(jsonPath("$.text").value(newComment.getText()));
        verify(itemService, only())
                .addComment(any(Comment.class), anyLong(), anyLong());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    @SneakyThrows
    public void testDelete() {
        mockMvc.perform(delete("/items/1"))
                .andExpect(status().isOk());
    }
}

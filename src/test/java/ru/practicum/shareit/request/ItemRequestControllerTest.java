package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    private static ItemRequest itemRequest1;
    private static ItemRequest itemRequest2;
    private static ItemRequest itemRequestWithItem;
    private static List<ItemRequest> itemRequestList;


    @BeforeAll
    public static void setup() {
        User user = User.builder()
                .id(1L)
                .email("simple@id.ru")
                .name("Dave")
                .build();
        itemRequest1 = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .description("Нужна лопата")
                .created(LocalDateTime.now())
                .build();
        itemRequest2 = ItemRequest.builder()
                .id(2L)
                .requester(user)
                .description("Нужна удочка")
                .created(LocalDateTime.now().plusMinutes(5))
                .build();
        itemRequestWithItem = ItemRequest.builder()
                .id(3L)
                .requester(user)
                .description("Нужна ручка")
                .created(LocalDateTime.now().plusMinutes(10))
                .items(List.of(Item.builder()
                        .id(1L)
                        .name("Ручка Erich Krause")
                        .build()))
                .build();
        itemRequestList = Arrays.asList(itemRequest1, itemRequest2, itemRequestWithItem);
    }

    @Test
    @SneakyThrows
    public void testGetAllByOwner() {
        when(itemRequestService.getAllByOwner(1L))
                .thenReturn(itemRequestList);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequest1.getId()))
                .andExpect(jsonPath("$[1].id").value(itemRequest2.getId()));
        verify(itemRequestService, only())
                .getAllByOwner(1L);
        verifyNoMoreInteractions(itemRequestService);

    }

    @Test
    @SneakyThrows
    public void testGetWithItem() {
        when(itemRequestService.get(itemRequestWithItem.getId(), 1L))
                .thenReturn(itemRequestWithItem);
        mockMvc.perform(get("/requests/3")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestWithItem.getId()))
                .andExpect(jsonPath("$.items.[0].id").value(itemRequestWithItem.getItems().get(0).getId()))
                .andExpect(jsonPath("$.items.[0].name").value(itemRequestWithItem.getItems().get(0).getName()));
        verify(itemRequestService, only())
                .get(itemRequestWithItem.getId(), 1L);
        verifyNoMoreInteractions(itemRequestService);

    }

    @Test
    @SneakyThrows
    public void testGetAllDefaultParams() {
        when(itemRequestService.getAll(1L, 0, 20))
                .thenReturn(itemRequestList);
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(itemRequest1.getId()))
                .andExpect(jsonPath("$[1].id").value(itemRequest2.getId()))
                .andExpect(jsonPath("$[2].id").value(itemRequestWithItem.getId()));
        verify(itemRequestService, only())
                .getAll(1L, 0, 20);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    @SneakyThrows
    public void testGetAllByUserId() {
        when(itemRequestService.add(any(ItemRequest.class), anyLong()))
                .thenReturn(itemRequest1);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ItemRequestDto.builder()
                                .description(itemRequest1.getDescription())
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequest1.getId()))
                .andExpect(jsonPath("$.requesterId").value(1L))
                .andExpect(jsonPath("$.description").value(itemRequest1.getDescription()));
        verify(itemRequestService, only())
                .add(any(ItemRequest.class), anyLong());
        verifyNoMoreInteractions(itemRequestService);

    }


}

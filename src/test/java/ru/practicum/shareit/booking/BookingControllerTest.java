package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    private static Booking booking;

    @BeforeAll
    public static void setup() {
        User user = User.builder()
                .id(1L)
                .email("simple@id.ru")
                .name("Dave")
                .build();
        booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusMinutes(20))
                .end(LocalDateTime.now().plusMinutes(120))
                .booker(user)
                .item(Item.builder()
                        .id(1L)
                        .name("Ручка Erich Krause")
                        .build())
                .build();

    }

    @Test
    @SneakyThrows
    public void testGet() {
        when(bookingService.get(anyLong(), anyLong()))
                .thenReturn(booking);
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value(booking.getStatus().name()))
                .andExpect(jsonPath("$.item.id").value(booking.getItem().getId()));
        verify(bookingService, only())
                .get(anyLong(), anyLong());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    public void testGetAllByUserId() {
        when(bookingService.getAllByUserId(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "2")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(booking.getId()))
                .andExpect(jsonPath("$[0].status").value(booking.getStatus().name()));
        verify(bookingService, only())
                .getAllByUserId(anyString(), anyLong(), anyInt(), anyInt());
        verifyNoMoreInteractions(bookingService);

    }

    @Test
    @SneakyThrows
    public void testAdd() {
        when(bookingService.add(any(Booking.class), anyLong()))
                .thenReturn(booking);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingMapper.toBookingDto(booking))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value(booking.getStatus().name()));
        verify(bookingService, only())
                .add(any(Booking.class), anyLong());
        verifyNoMoreInteractions(bookingService);

    }

    @Test
    @SneakyThrows
    public void testAddIncorrectStartTime() {
        Booking current = Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusMinutes(20))
                .end(LocalDateTime.now().plusMinutes(120))
                .item(Item.builder()
                        .id(1L)
                        .name("Ручка Erich Krause")
                        .build())
                .build();
        when(bookingService.add(any(Booking.class), anyLong()))
                .thenReturn(current);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingMapper.toBookingDto(current))))
                .andExpect(status().isBadRequest());
        verify(bookingService, never())
                .add(any(Booking.class), anyLong());
        verifyNoMoreInteractions(bookingService);

    }

    @Test
    @SneakyThrows
    public void testUpdate() {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("bookingId", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()));
        verify(bookingService, only())
                .update(anyLong(), anyLong(), anyBoolean());
        verifyNoMoreInteractions(bookingService);

    }

    @Test
    @SneakyThrows
    public void testGetAllByItemOwner() {
        when(bookingService.getAllByItemOwner(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "2")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(booking.getId()))
                .andExpect(jsonPath("$[0].status").value(booking.getStatus().name()));
        verify(bookingService, only())
                .getAllByItemOwner(anyString(), anyLong(), anyInt(), anyInt());
        verifyNoMoreInteractions(bookingService);

    }

}

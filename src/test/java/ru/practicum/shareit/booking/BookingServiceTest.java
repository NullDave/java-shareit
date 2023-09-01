package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private static Booking booking;
    private static User user;
    private static User user2;
    private static Item item;

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
                .name("Jon Dou")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Ручка Erich Krause")
                .owner(user2)
                .available(true)
                .build();
        booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusMinutes(20))
                .end(LocalDateTime.now().plusMinutes(120))
                .booker(user)
                .item(item)
                .build();
    }

    @Test
    public void testGet() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Booking currentBooking = bookingService.get(1L, 1L);
        assertEquals(currentBooking.getId(), booking.getId());
        assertEquals(currentBooking.getItem(), booking.getItem());
        assertEquals(currentBooking.getBooker(), booking.getBooker());
        assertEquals(currentBooking.getStatus(), booking.getStatus());

    }

    @Test
    public void testGetWithIncorrectUserId() {
        User newUser = User.builder()
                .id(3L)
                .email("mail@ya.ru")
                .name("Lisa Fig").build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(newUser));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () -> bookingService.get(1L, 3L));

    }

    @Test
    public void testGetAllByUserId() {
        Booking booking2 = Booking.builder()
                .id(2L)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusMinutes(300))
                .end(LocalDateTime.now().plusMinutes(360))
                .booker(user)
                .item(item)
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.countAllByBookerId(anyLong()))
                .thenReturn(2);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking, booking2));
        List<Booking> currentList = bookingService.getAllByUserId(BookingState.ALL.name(), 1L, 0, 20);
        assertEquals(currentList.get(0).getId(), booking.getId());
        assertEquals(currentList.get(1).getId(), booking2.getId());
        assertEquals(currentList.get(1).getBooker().getId(), user.getId());
    }

    @Test
    public void testAllByItemOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking));
        List<Booking> currentList = bookingService.getAllByItemOwner(BookingState.ALL.name(), 2L, 0, 20);
        assertEquals(currentList.get(0).getId(), booking.getId());
        assertEquals(currentList.get(0).getBooker().getId(), user.getId());
        assertEquals(currentList.get(0).getItem().getOwner().getId(), user2.getId());

    }

    @Test
    public void testUpdate() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        Booking currentBooking = bookingService.update(1L, 2L, true);
        assertEquals(currentBooking.getId(), booking.getId());
        assertEquals(currentBooking.getItem(), booking.getItem());
        assertEquals(currentBooking.getBooker(), booking.getBooker());
        assertEquals(currentBooking.getStatus(), BookingStatus.APPROVED);

    }

    @Test
    public void testUpdateWithIncorrectStatus() {
        Booking booking2 = Booking.builder()
                .id(2L)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusMinutes(300))
                .end(LocalDateTime.now().plusMinutes(360))
                .booker(user)
                .item(item)
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking2));
        assertThrows(BadRequestException.class, () -> bookingService.update(1L, 2L, true));

    }

    @Test
    public void testAdd() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        Booking currentBooking = bookingService.add(booking, 1L);
        assertEquals(currentBooking.getId(), booking.getId());
        assertEquals(currentBooking.getItem(), booking.getItem());
        assertEquals(currentBooking.getBooker(), booking.getBooker());
        assertEquals(currentBooking.getStatus(), BookingStatus.WAITING);

    }

    @Test
    public void testAddWithIncorrectUserId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> bookingService.add(booking, 1L));

    }

    @Test
    public void testAddWithIncorrectBooker() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> bookingService.add(booking, 2L));

    }

    @Test
    public void testAddWithIncorrectTime() {
        Booking booking2 = Booking.builder()
                .id(2L)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusMinutes(300))
                .end(LocalDateTime.now().plusMinutes(290))
                .booker(user)
                .item(item)
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        assertThrows(BadRequestException.class, () -> bookingService.add(booking2, 1L));

    }
}

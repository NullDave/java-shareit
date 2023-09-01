package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private static User user;
    private static User user2;
    private static Item item;
    private static Item item2;
    private static ItemRequest itemRequest;
    private static Comment comment;

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
        item = Item.builder()
                .id(1L)
                .name("Ручка Erich Krause")
                .description("Выиграл в \"СВОЯ ИГРА\" инкрустирована бриллиантами.")
                .available(true)
                .owner(user)
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .description("Нужна лопата")
                .created(LocalDateTime.now())
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("Лопата")
                .description("Лопата Деда.")
                .owner(user2)
                .request(itemRequest)
                .available(false)
                .build();
        comment = Comment.builder()
                .id(1L)
                .author(user)
                .text("Лопата топ")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    public void testGetAllByUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(List.of(item));
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(null);
        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(null);
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(Collections.emptyList());
        List<ItemDto> currentList = itemService.getAllByUser(1L, 0, 20);
        assertEquals(currentList.size(), 1);
        assertEquals(currentList.get(0).getId(), item.getId());
        assertEquals(currentList.get(0).getName(), item.getName());
    }

    @Test
    public void testGet() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item2));
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(null);
        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(null);
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(Collections.emptyList());
        ItemDto currentItem = itemService.get(2L, 2L);
        assertEquals(currentItem.getId(), item2.getId());
        assertEquals(currentItem.getName(), item2.getName());
        assertEquals(currentItem.getAvailable(), item2.getAvailable());

    }

    @Test
    public void testGetWithLastBooking() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item2));
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(null);
        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(Booking.builder()
                        .id(1L)
                        .start(LocalDateTime.now().minusMinutes(300))
                        .end(LocalDateTime.now().plusMinutes(100))
                        .booker(user)
                        .status(BookingStatus.APPROVED)
                        .build());
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(Collections.emptyList());
        ItemDto currentItem = itemService.get(2L, 2L);
        assertEquals(currentItem.getId(), item2.getId());
        assertEquals(currentItem.getName(), item2.getName());
        assertEquals(currentItem.getAvailable(), item2.getAvailable());
        assertEquals(currentItem.getLastBooking().getId(), 1L);
        assertEquals(currentItem.getLastBooking().getBookerId(), user.getId());


    }

    @Test
    public void testGetWithIncorrectItemId() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.get(3L, 1L));

    }

    @Test
    public void testAdd() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        Item currentItem = itemService.add(item, 1L);
        assertEquals(currentItem.getId(), item.getId());
        assertEquals(currentItem.getName(), item.getName());
        assertEquals(currentItem.getAvailable(), item.getAvailable());

    }

    @Test
    public void testAddWithRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item2);
        Item currentItem = itemService.add(item2, 1L);
        assertEquals(currentItem.getId(), item2.getId());
        assertEquals(currentItem.getName(), item2.getName());
        assertEquals(currentItem.getAvailable(), item2.getAvailable());
        assertEquals(currentItem.getRequest().getId(), item2.getRequest().getId());
    }

    @Test
    public void testUpdate() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        Item currentItem = itemService.update(item, 1L);
        assertEquals(currentItem.getId(), item.getId());
        assertEquals(currentItem.getName(), item.getName());
        assertEquals(currentItem.getAvailable(), item.getAvailable());

    }

    @Test
    public void testUpdateWithIncorrectUser() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        assertThrows(PermissionException.class, () -> itemService.update(item, 2L));

    }

    @Test
    public void testSearch() {
        when(itemRepository.search(anyString(), any()))
                .thenReturn(List.of(item));
        List<Item> currentList = itemService.search("ручка", 0, 20);
        assertEquals(currentList.size(), 1);
        assertEquals(currentList.get(0).getId(), item.getId());
        assertEquals(currentList.get(0).getName(), item.getName());
    }

    @Test
    public void testAddComment() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item2));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndEndBeforeAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.APPROVED)
                        .build());
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);
        Comment currentComment = itemService.addComment(comment, 2L, 1L);
        assertEquals(currentComment.getId(), comment.getId());
        assertEquals(currentComment.getAuthor(), comment.getAuthor());
        assertEquals(currentComment.getText(), comment.getText());
    }

    @Test
    public void testWithIncorrectUser() {
        when(bookingRepository.findFirstByItemIdAndBookerIdAndEndBeforeAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(null);
        assertThrows(BadRequestException.class, () -> itemService.addComment(comment, 2L, 2L));
    }

}

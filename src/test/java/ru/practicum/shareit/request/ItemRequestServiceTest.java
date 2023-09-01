package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private static User user;
    private static ItemRequest itemRequest;
    private static ItemRequest itemRequest2;
    private static ItemRequest itemRequestWithItem;
    private static List<ItemRequest> itemRequestList;

    @BeforeAll
    public static void setup() {

        user = User.builder()
                .id(1L)
                .email("simple@id.ru")
                .name("Dave")
                .build();
        itemRequest = ItemRequest.builder()
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
        itemRequestList = Arrays.asList(itemRequest, itemRequest2, itemRequestWithItem);
    }


    @Test
    public void testGetAllByOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong()))
                .thenReturn(itemRequestList);
        List<ItemRequest> currentList = itemRequestService.getAllByOwner(1L);

        assertEquals(currentList.get(0).getId(), itemRequest.getId());
        assertEquals(currentList.get(1).getId(), itemRequest2.getId());
        assertEquals(currentList.get(2).getId(), itemRequestWithItem.getId());
        assertEquals(currentList.get(2).getItems().get(0).getId(), 1L);

    }

    @Test
    public void testGetAllByOwnerWithIncorrectId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllByOwner(1L));
    }

    @Test
    public void testGet() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequestWithItem));
        ItemRequest currentItemRequest = itemRequestService.get(1L, 1L);

        assertEquals(currentItemRequest, itemRequestWithItem);

    }

    @Test
    public void testGetWithIncorrectItemRequestId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.get(2L, 1L));

    }

    @Test
    public void testAdd() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequest currentItemRequest = itemRequestService.add(itemRequest, 1L);
        assertEquals(currentItemRequest.getId(), itemRequest.getId());
        assertEquals(currentItemRequest.getRequester().getId(), user.getId());
        assertEquals(currentItemRequest.getDescription(), itemRequest.getDescription());
    }

}

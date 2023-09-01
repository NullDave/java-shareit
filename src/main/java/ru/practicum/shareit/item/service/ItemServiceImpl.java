package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemDto> getAllByUser(Long userId, Integer from, Integer size) {
        getUserById(userId);
        Pageable page = PageRequest.of(from, size);
        List<ItemDto> itemDtos = itemRepository.findAllByOwnerId(userId, page).stream()
                .map(ItemMapper::toItemDto)
                .peek(itemDto -> {
                    itemDto.setNextBooking(getNextBooking(itemDto.getId()));
                    itemDto.setLastBooking(getLastBooking(itemDto.getId()));
                    itemDto.setComments(getComments(itemDto.getId()));
                })
                .collect(Collectors.toList());

        return itemDtos;
    }

    @Override
    public ItemDto get(Long itemId, Long userId) {
        Item item = getItem(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            itemDto.setNextBooking(getNextBooking(itemId));
            itemDto.setLastBooking(getLastBooking(itemId));
        }
        itemDto.setComments(getComments(itemId));
        return itemDto;
    }

    @Override
    @Transactional
    public Item add(Item item, Long userId) {
        User user = getUserById(userId);
        item.setOwner(user);
        if (item.getRequest() != null) {
            item.setRequest(itemRequestRepository.findById(item.getRequest().getId())
                    .orElseThrow(() -> new NotFoundException("запрос не найден id:" + item.getRequest().getId())));
        }
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item update(Item item, Long userId) {
        if (!getItem(item.getId()).getOwner().getId().equals(userId)) {
            throw new PermissionException("Пользователь не являющегося собственником");
        }

        User user = getUserById(userId);
        item.setOwner(user);
        Item currentItem = getItem(item.getId());
        if (item.getName() != null && !item.getName().isBlank()) {
            currentItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            currentItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            currentItem.setAvailable(item.getAvailable());
        }
        return itemRepository.save(currentItem);
    }

    @Override
    @Transactional
    public void delete(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<Item> search(String keyword, Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size);
        if (keyword.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(keyword.toLowerCase(), page);
    }

    @Override
    @Transactional
    public Comment addComment(Comment comment, Long itemId, Long userId) {
        validAuthor(itemId, userId);
        comment.setItem(getItem(itemId));
        comment.setAuthor(getUserById(userId));
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("предмет не найден:" + itemId));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь не найден id:" + userId));
    }

    private void validAuthor(Long itemId, Long userId) {
        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndEndBeforeAndStatus(itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (booking == null)
            throw new BadRequestException("Пользователь не имеет право оставлять комментарий для этого предмета");
    }

    private BookingForItemDto getNextBooking(Long itemId) {
        Booking booking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        return booking != null ? BookingMapper.toBookingForItemDto(booking) : null;
    }

    private BookingForItemDto getLastBooking(Long itemId) {
        Booking booking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        return booking != null ? BookingMapper.toBookingForItemDto(booking) : null;
    }

    private List<CommentDto> getComments(Long itemId) {
        return commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}

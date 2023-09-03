package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotImplementedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking get(Long bookingId, Long userId) {
        getUser(userId);
        Booking booking = getBookingById(bookingId);
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new NotFoundException("Нет право на просмотр запроса для пользователя: " + userId);
        }
        return booking;
    }

    @Override
    public List<Booking> getAllByUserId(String state, Long userId, Integer from, Integer size) {
        getUser(userId);
        Pageable page = PageRequest.of(from / size, size);
        switch (BookingState.get(state)) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page);
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), page);
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), page);
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), page);
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, page);
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, page);
            default:
                throw new NotImplementedException("Unknown state: " + state);
        }
    }

    @Override
    public List<Booking> getAllByItemOwner(String state, Long userId, Integer from, Integer size) {
        getUser(userId);
        Pageable page = PageRequest.of(from, size);
        switch (BookingState.get(state)) {
            case ALL:
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, page);
            case CURRENT:
                return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), page);
            case PAST:
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), page);
            case FUTURE:
                return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), page);
            case WAITING:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, page);
            case REJECTED:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, page);
            default:
                throw new NotImplementedException("Unknown state: " + state);
        }
    }

    @Override
    @Transactional
    public Booking update(Long bookingId, Long userId, Boolean approved) {
        getUser(userId);
        Booking booking = getBookingById(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("нет право на подтверждения запроса");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("статус запрос не ожидание");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking add(Booking booking, Long userId) {
        Item item = getItem(booking.getItem().getId());
        if (!item.getAvailable()) {
            throw new BadRequestException("предмет не доступен для бронирования");
        }
        User booker = getUser(userId);
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("нельзя арендовать у себя");
        }
        if (!booking.getEnd().isAfter(booking.getStart())) {
            throw new BadRequestException("время окончания не после старта");
        }
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("пользователь не найден id:" + id));
    }

    private Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("предмет не найден id:" + id));
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("запрос не найден:" + id));
    }
}

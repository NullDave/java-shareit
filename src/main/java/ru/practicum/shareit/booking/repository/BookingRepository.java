package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime endDateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime startDateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus bookingStatus, Pageable pageable);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime endDateTime, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime startDateTime, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    Booking findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime currentTime, BookingStatus status);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime currentTime, BookingStatus status);

    Booking findFirstByItemIdAndBookerIdAndEndBeforeAndStatus(Long itemId, Long userId, LocalDateTime currentTime, BookingStatus status);

    Integer countAllByBookerId(Long userId);
}
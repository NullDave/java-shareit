package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime endDateTime);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime startDateTime);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus bookingStatus);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime endDateTime);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime startDateTime);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    Booking findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime currentTime, BookingStatus status);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime currentTime, BookingStatus status);

    Booking findFirstByItemIdAndBookerIdAndEndBeforeAndStatus(Long itemId, Long userId, LocalDateTime currentTime, BookingStatus status);
}
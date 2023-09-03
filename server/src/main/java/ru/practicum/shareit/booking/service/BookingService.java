package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking get(Long bookingId, Long userId);

    List<Booking> getAllByUserId(String state, Long userId, Integer from, Integer size);

    List<Booking> getAllByItemOwner(String state, Long userId, Integer from, Integer size);

    Booking update(Long bookingId, Long userId, Boolean approved);

    Booking add(Booking booking, Long userId);

}

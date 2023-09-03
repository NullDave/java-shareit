package ru.practicum.shareit.booking.model;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED,
    UNKNOWN;

    public static BookingState get(String state) {
        for (BookingState bookingState : values()) {
            if (bookingState.name().equals(state)) {
                return bookingState;
            }
        }
        return BookingState.UNKNOWN;
    }
}

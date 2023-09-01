package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingResponseDto get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId) {
        return BookingMapper.toBookingResponseDto(bookingService.get(bookingId, userId));
    }

    @GetMapping
    public List<BookingResponseDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                   @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return bookingService.getAllByUserId(state, userId, from, size).stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public BookingResponseDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Valid @RequestBody BookingDto bookingDto) {
        return BookingMapper.toBookingResponseDto(bookingService.add(BookingMapper.toBooking(bookingDto), userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return BookingMapper.toBookingResponseDto(bookingService.update(bookingId, userId, approved));
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByItemOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                      @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return bookingService.getAllByItemOwner(state, userId, from, size).stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }
}

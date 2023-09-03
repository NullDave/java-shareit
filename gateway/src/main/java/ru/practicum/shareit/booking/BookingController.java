package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId,
											 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.get(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getAllByUserId(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllByItemOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
												 @RequestParam(name = "state", defaultValue = "all") String stateParam,
												 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
												 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking by item owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getAllByItemOwner(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.add(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public  ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
										  @PathVariable Long bookingId, @RequestParam Boolean approved) {
		log.info("Update booking status approved = {}, userId={}, bookingId={}",approved, userId, bookingId);
		return bookingClient.update(userId,bookingId, approved);
	}

}

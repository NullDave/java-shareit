package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/requests")
@AllArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long requestId) {
        log.info("Get request by id={}, userId={}", requestId, userId);
        return requestClient.get(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all request by owner userId={}", userId);
        return requestClient.getAllByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Get all request by user userId={}, from={}, size={}", userId, from, size);
        return requestClient.getAll(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Creating request {}, userId={}", itemRequestDto, userId);
        return requestClient.add(userId, itemRequestDto);
    }

}

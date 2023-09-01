package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getAllByOwner(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllByOwner(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto get(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                              @PathVariable Long requestId) {
        return ItemRequestMapper.toItemRequestDto(itemRequestService.get(requestId, userId));
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                       @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                       @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return itemRequestService.getAll(userId, from, size).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemRequestDto add(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return ItemRequestMapper.toItemRequestDto(
                itemRequestService.add(ItemRequestMapper.toItemRequest(itemRequestDto),
                        userId));
    }

}

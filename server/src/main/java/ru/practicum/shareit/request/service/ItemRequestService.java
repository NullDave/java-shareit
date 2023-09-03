package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest get(Long requestId, Long userId);

    List<ItemRequest> getAll(Long userId, Integer from, Integer size);

    List<ItemRequest> getAllByOwner(Long userId);

    ItemRequest add(ItemRequest itemRequest, Long userId);
}

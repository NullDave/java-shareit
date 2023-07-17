package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> getAllByUser(Long userId);

    Item get(Long itemId);

    Item add(Item item, Long userId);

    Item update(Item item, Long userId);

    void delete(Long itemId);

    List<Item> search(String text);
}

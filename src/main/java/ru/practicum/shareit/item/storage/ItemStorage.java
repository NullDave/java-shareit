package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    List<Item> getAllByUser(Long userId);

    Item get(Long itemId);

    Item add(Item item);

    Item update(Item item);

    void delete(Long itemId);

    List<Item> search(String text);

}

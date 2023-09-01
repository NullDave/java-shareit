package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllByUser(Long userId, Integer from, Integer size);

    ItemDto get(Long itemId, Long userId);

    Item add(Item item, Long userId);

    Item update(Item item, Long userId);

    void delete(Long itemId);

    List<Item> search(String keyword, Integer from, Integer size);

    Comment addComment(Comment comment, Long itemId, Long userId);
}

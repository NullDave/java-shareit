package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items;
    private long countItem = 1;


    @Override
    public List<Item> getAllByUser(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item get(Long itemId) {
        Item currentItem = items.get(itemId);
        if (currentItem == null) throw new NotFoundException("предмет не найден:" + itemId);
        return currentItem;
    }

    @Override
    public Item add(Item item) {
        item.setId(countItem++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        Item currentItem = get(item.getId());
        if (item.getName() != null && !item.getName().isBlank()) {
            currentItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            currentItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            currentItem.setAvailable(item.getAvailable());
        }
        return currentItem;
    }

    @Override
    public void delete(Long itemId) {
        items.remove(itemId);
    }

    @Override
    public List<Item> search(String text) {
        return items.values()
                .stream()
                .filter(item -> !text.isBlank() &&
                        item.getDescription().toLowerCase().contains(text.toLowerCase()) &&
                        item.getAvailable())
                .collect(Collectors.toList());
    }


}

package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private ItemStorage itemStorage;
    private UserStorage userStorage;


    @Override
    public List<Item> getAllByUser(Long userId) {
        return itemStorage.getAllByUser(userId);
    }

    @Override
    public Item get(Long itemId) {
        return itemStorage.get(itemId);
    }

    @Override
    public Item add(Item item, Long userId) {
        User user = userStorage.get(userId);
        item.setOwner(user);
        return itemStorage.add(item);
    }

    @Override
    public Item update(Item item, Long userId) {
        User user = userStorage.get(userId);
        if (!itemStorage.get(item.getId()).getOwner().getId().equals(userId)) {
            throw new PermissionException("Пользователь не найден");
        }
        item.setOwner(user);
        return itemStorage.update(item);
    }

    @Override
    public void delete(Long itemId) {
        itemStorage.delete(itemId);
    }

    @Override
    public List<Item> search(String text) {
        return itemStorage.search(text);
    }
}

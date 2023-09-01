package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testSearch() {
        User user = User.builder()
                .email("simple@id.ru")
                .name("Dave")
                .build();
        entityManager.persist(user);
        Item item = Item.builder()
                .name("Ручка Erich Krause")
                .description("Выиграл в \"СВОЯ ИГРА\" инкрустирована бриллиантами.")
                .available(true)
                .owner(user)
                .build();
        entityManager.persist(item);
        Pageable page = PageRequest.of(0, 20);
        List<Item> currentList = itemRepository.search("erich", page);
        assertEquals(currentList.size(), 1);
        assertEquals(currentList.get(0).getId(), item.getId());
        assertEquals(currentList.get(0).getName(), item.getName());

    }
}

package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long userId, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE %:keyword% OR LOWER(i.description) LIKE %:keyword% AND i.available=true")
    List<Item> search(@Param("keyword") String keyword, Pageable pageable);

}

package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;


    @Override
    public ItemRequest get(Long requestId, Long userId) {
        getUser(userId);
        return getItemRequest(requestId);
    }

    @Override
    public List<ItemRequest> getAll(Long userId, Integer from, Integer size) {
        getUser(userId);
        Pageable page = PageRequest.of(from, size);
        return itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, page);
    }

    @Override
    public List<ItemRequest> getAllByOwner(Long userId) {
        getUser(userId);
        return itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
    }

    @Override
    @Transactional
    public ItemRequest add(ItemRequest itemRequest, Long userId) {
        User user = getUser(userId);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    private ItemRequest getItemRequest(Long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("запрос не найден id:" + id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("пользователь не найден id:" + id));
    }
}

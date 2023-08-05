package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllByUser(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.getAllByUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                       @PathVariable Long itemId) {
        return itemService.get(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto add(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.add(ItemMapper.toItem(itemDto), userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return ItemMapper.toItemDto(itemService.update(ItemMapper.toItem(itemDto), userId));
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        itemService.delete(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return CommentMapper.toCommentDto(itemService.addComment(CommentMapper.toComment(commentDto), itemId, userId));
    }
}

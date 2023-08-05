package ru.practicum.shareit.item.dto;


import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentDto> comments;
}

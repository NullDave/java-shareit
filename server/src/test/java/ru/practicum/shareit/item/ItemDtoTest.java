package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> itemDtoJacksonTester;

    @Test
    @SneakyThrows
    public void testItemDtoToJson() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Ручка Erich Krause")
                .description("Выиграл в \"СВОЯ ИГРА\" инкрустирована бриллиантами.")
                .available(true)
                .requestId(1L)
                .lastBooking(BookingForItemDto.builder()
                        .id(1L)
                        .bookerId(2L)
                        .start(LocalDateTime.now().minusMinutes(300))
                        .end(LocalDateTime.now().minusMinutes(200))
                        .build())
                .comments(List.of(CommentDto.builder()
                        .id(1L)
                        .authorName("Dave")
                        .text("Отличная Ручка")
                        .created(LocalDateTime.now())
                        .build()))
                .build();
        JsonContent<ItemDto> jsonContent = itemDtoJacksonTester.write(itemDto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.comments.[0].id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(2);

    }
}

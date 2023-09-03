package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingResponseDtoTest {
    @Autowired
    private JacksonTester<BookingResponseDto> bookingResponseDtoJacksonTester;

    @Test
    @SneakyThrows
    public void testBookingResponseDtoToJson() {
        User user = User.builder()
                .id(1L)
                .email("simple@id.ru")
                .name("Dave")
                .build();
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusMinutes(20))
                .end(LocalDateTime.now().plusMinutes(120))
                .booker(user)
                .item(Item.builder()
                        .id(1L)
                        .name("Ручка Erich Krause")
                        .build())
                .build();

        JsonContent<BookingResponseDto> jsonContent = bookingResponseDtoJacksonTester.write(bookingResponseDto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.status").isEqualTo(BookingStatus.WAITING.name());
        assertThat(jsonContent).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.name").isEqualTo("Ручка Erich Krause");

    }
}

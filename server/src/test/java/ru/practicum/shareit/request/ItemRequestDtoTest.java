package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestDtoJacksonTester;

    @Test
    @SneakyThrows
    public void testItemRequestDtoToJson() {
        User user = User.builder()
                .id(1L)
                .email("simple@id.ru")
                .name("Dave")
                .build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .requesterId(user.getId())
                .description("Нужна лопата")
                .created(LocalDateTime.now())
                .build();

        JsonContent<ItemRequestDto> jsonContent = itemRequestDtoJacksonTester.write(itemRequestDto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requesterId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
    }

}

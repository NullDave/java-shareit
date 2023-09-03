package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> userDtoJacksonTester;

    @Test
    @SneakyThrows
    public void testUserDtoToJson() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("simple@id.ru")
                .name("Dave")
                .build();
        JsonContent<UserDto> jsonContent = userDtoJacksonTester.write(userDto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
    }
}

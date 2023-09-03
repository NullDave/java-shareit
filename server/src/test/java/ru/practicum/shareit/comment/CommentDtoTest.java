package ru.practicum.shareit.comment;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> commentDtoJacksonTester;

    @Test
    @SneakyThrows
    public void testCommentDtoToJson() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorName("Dave")
                .text("Отличная лопата")
                .created(LocalDateTime.now())
                .build();

        JsonContent<CommentDto> jsonContent = commentDtoJacksonTester.write(commentDto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDto.getAuthorName());
        assertThat(jsonContent).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
    }
}

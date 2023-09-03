package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.*;

@Data
@Builder
public class BookItemRequestDto {
	private Long id;
	@NotNull
	private Long itemId;
	@NotNull
	@FutureOrPresent
	private LocalDateTime start;
	@NotNull
	@Future
	private LocalDateTime end;
}

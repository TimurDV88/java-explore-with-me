package ru.practicum.ewm.event.dto;

import lombok.Data;
import ru.practicum.ewm.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class NewEventDto {

    @NotNull
    @NotBlank
    private final String annotation;

    @NotNull
    private final Long categoryId;

    @NotNull
    @NotBlank
    private final String description;

    @NotNull
    private final String eventDate;

    @NotNull
    private final Location location;

    private final Boolean paid;
    private final Integer participantLimit;
    private final Boolean requestModeration;

    @NotNull
    @NotBlank
    private final String title;
}

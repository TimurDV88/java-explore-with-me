package ru.practicum.ewm.event.dto;

import lombok.Data;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
public class EventFullDto {

    private final Long id;

    private final String annotation;

    private final CategoryDto categoryDto;

    private final Integer confirmedRequests;

    private final LocalDateTime createdOn;

    private final String description;

    private final LocalDateTime eventDate;

    private final UserShortDto initiator;

    private final Location location;

    private final Boolean paid;

    private final Integer participantLimit;

    private final LocalDateTime publishedOn;

    private final Boolean requestModeration;

    private final EventState state;

    private final String title;

    private final Integer views;
}

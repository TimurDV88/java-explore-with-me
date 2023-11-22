package ru.practicum.ewm.event.dto;

import lombok.Data;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

@Data
public class EventShortDto {

    private final Long id;
    private final String annotation;
    private final CategoryDto category;
    private final Integer confirmedRequests;
    private final String eventDate;
    private final UserShortDto initiator;
    private final Boolean paid;
    private final String title;
    private final Integer views;
    private final Integer rating;
}

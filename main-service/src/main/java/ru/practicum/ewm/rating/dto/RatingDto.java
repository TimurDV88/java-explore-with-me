package ru.practicum.ewm.rating.dto;

import lombok.Data;

@Data
public class RatingDto {

    private final Long id;

    private final Long eventId;

    private final Long userId;

    private final Integer rating;
}

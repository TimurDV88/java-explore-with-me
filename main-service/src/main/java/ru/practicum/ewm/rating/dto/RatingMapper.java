package ru.practicum.ewm.rating.dto;

import ru.practicum.ewm.rating.model.Rating;

public class RatingMapper {

    public static RatingDto ratingToDto(Rating rating) {

        return new RatingDto(

                rating.getId(),
                rating.getEventId(),
                rating.getUserId(),
                rating.getRating()
        );
    }
}

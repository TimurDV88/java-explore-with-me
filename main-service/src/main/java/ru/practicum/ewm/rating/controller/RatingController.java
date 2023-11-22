package ru.practicum.ewm.rating.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.rating.service.RatingService;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/ratings")
public class RatingController {

    private final RatingService ratingService;

    @PutMapping("/like")
    public RatingDto setLike(@RequestParam(value = "eventId") Long eventId,
                             @RequestParam(value = "userId") Long userId) {

        return ratingService.setLike(eventId, userId);
    }

    @PutMapping("/dislike")
    public RatingDto setDislike(@RequestParam(value = "eventId") Long eventId,
                                @RequestParam(value = "userId") Long userId) {

        return ratingService.setDislike(eventId, userId);
    }

    @GetMapping("/users")
    List<UserShortDto> getUsersRating(@RequestParam(value = "from", defaultValue = "0") int from,
                                      @RequestParam(value = "size", defaultValue = "10") int size) {

        return ratingService.getUsersRating(from, size);
    }

    @GetMapping("/events")
    List<EventShortDto> getEventsRating(@RequestParam(value = "from", defaultValue = "0") int from,
                                        @RequestParam(value = "size", defaultValue = "10") int size) {

        return ratingService.getEventsRating(from, size);
    }
}

package ru.practicum.ewm.rating.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.controller.CategoryAdminController;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.event.controller.EventAdminController;
import ru.practicum.ewm.event.controller.EventPrivateController;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.participationRequest.controller.PartRequestController;
import ru.practicum.ewm.rating.repository.RatingRepository;
import ru.practicum.ewm.user.controller.UserAdminController;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.UserFullDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.ewm.event.dto.UpdateEventAdminRequest.StateAction.PUBLISH_EVENT;

import java.util.List;
@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RatingServiceTest {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final RatingRepository ratingRepository;
    private final RatingService ratingService;

    private final EventAdminController eventAdminController;
    private final PartRequestController partRequestController;
    private final EventPrivateController eventPrivateController;
    private final CategoryAdminController categoryAdminController;
    private final UserAdminController userAdminController;

    private static EventFullDto eventFullDto1;
    private static EventFullDto eventFullDto2;
    private static EventFullDto eventFullDto3;
    private static EventFullDto eventFullDto4;
    private static UserFullDto userFullDto1;
    private static UserFullDto userFullDto2;
    private static UserFullDto userFullDto3;


    @BeforeEach
    void setUp() {

        userFullDto1 = userAdminController.add(new NewUserDto("user_name1", "email1@email.com"));

        userFullDto2 = userAdminController.add(new NewUserDto("user_name2", "email2@email.com"));

        userFullDto3 = userAdminController.add(new NewUserDto("user_name3", "email3@email.com"));

        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("category_name");
        Long category = categoryAdminController.add(newCategoryDto).getId();

        String eventDate = LocalDateTime.now().plusDays(1).format(EventMapper.DATE_TIME_FORMATTER);

        Location location = new Location();
        location.setLat(100);
        location.setLon(100);

        Boolean paid = true;
        Integer participantLimit = 100;
        Boolean requestModeration = false;

        // event1
        NewEventDto newEventDto = new NewEventDto("annotationEvent1", category, "descriptionEvent1",
                eventDate, location, paid, participantLimit, requestModeration, "titleEvent1");
        eventFullDto1 = eventPrivateController.add(userFullDto1.getId(), newEventDto);

        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setStateAction(PUBLISH_EVENT);
        eventAdminController.updateEventByAdmin(eventFullDto1.getId(), updateRequest);

        partRequestController.add(userFullDto2.getId(), eventFullDto1.getId());
        partRequestController.add(userFullDto3.getId(), eventFullDto1.getId());

        // event2
        newEventDto = new NewEventDto("annotationEvent2", category, "descriptionEvent2",
                eventDate, location, paid, participantLimit, requestModeration, "titleEvent2");
        eventFullDto2 = eventPrivateController.add(userFullDto2.getId(), newEventDto);

        eventAdminController.updateEventByAdmin(eventFullDto2.getId(), updateRequest);

        partRequestController.add(userFullDto1.getId(), eventFullDto2.getId());
        partRequestController.add(userFullDto3.getId(), eventFullDto2.getId());

        // event3
        newEventDto = new NewEventDto("annotationEvent3", category, "descriptionEvent3",
                eventDate, location, paid, participantLimit, requestModeration, "titleEvent3");
        eventFullDto3 = eventPrivateController.add(userFullDto3.getId(), newEventDto);

        eventAdminController.updateEventByAdmin(eventFullDto3.getId(), updateRequest);

        partRequestController.add(userFullDto1.getId(), eventFullDto3.getId());
        partRequestController.add(userFullDto2.getId(), eventFullDto3.getId());

        // event4
        newEventDto = new NewEventDto("annotationEvent4", category, "descriptionEvent4",
                eventDate, location, paid, participantLimit, requestModeration, "titleEvent4");
        eventFullDto4 = eventPrivateController.add(userFullDto1.getId(), newEventDto);

        eventAdminController.updateEventByAdmin(eventFullDto4.getId(), updateRequest);

        partRequestController.add(userFullDto2.getId(), eventFullDto4.getId());
        partRequestController.add(userFullDto3.getId(), eventFullDto4.getId());
    }

    @Test
    void setLikeAndDislike() {

        //like
        ratingService.setLike(eventFullDto1.getId(), userFullDto2.getId());
        assertEquals(1, eventRepository.findById(eventFullDto1.getId()).get().getRating());

        ratingService.setLike(eventFullDto1.getId(), userFullDto2.getId());
        assertEquals(0, eventRepository.findById(eventFullDto1.getId()).get().getRating());

        //dislike
        ratingService.setDislike(eventFullDto1.getId(), userFullDto2.getId());
        assertEquals(-1, eventRepository.findById(eventFullDto1.getId()).get().getRating());

        ratingService.setDislike(eventFullDto1.getId(), userFullDto2.getId());
        assertEquals(0, eventRepository.findById(eventFullDto1.getId()).get().getRating());

        //mix
        ratingService.setDislike(eventFullDto1.getId(), userFullDto2.getId());
        assertEquals(-1, eventRepository.findById(eventFullDto1.getId()).get().getRating());

        ratingService.setLike(eventFullDto1.getId(), userFullDto2.getId());
        assertEquals(1, eventRepository.findById(eventFullDto1.getId()).get().getRating());

        ratingService.setDislike(eventFullDto1.getId(), userFullDto2.getId());
        assertEquals(-1, eventRepository.findById(eventFullDto1.getId()).get().getRating());

        ratingService.setDislike(eventFullDto1.getId(), userFullDto3.getId());
        assertEquals(-2, eventRepository.findById(eventFullDto1.getId()).get().getRating());

        ratingService.setDislike(eventFullDto1.getId(), userFullDto3.getId());
        assertEquals(-1, eventRepository.findById(eventFullDto1.getId()).get().getRating());

        ratingService.setDislike(eventFullDto1.getId(), userFullDto3.getId());
        assertEquals(-2, eventRepository.findById(eventFullDto1.getId()).get().getRating());

        ratingService.setLike(eventFullDto1.getId(), userFullDto3.getId());
        assertEquals(0, eventRepository.findById(eventFullDto1.getId()).get().getRating());
    }

    @Test
    void getUsersAndEventsRating() {

        ratingService.setLike(eventFullDto1.getId(), userFullDto2.getId());
        ratingService.setLike(eventFullDto1.getId(), userFullDto3.getId());
        ratingService.setLike(eventFullDto2.getId(), userFullDto1.getId());
        ratingService.setDislike(eventFullDto3.getId(), userFullDto2.getId());

        // users
        List<UserShortDto> usersRating = ratingService.getUsersRating(0,10);

        assertEquals(2, usersRating.get(0).getRating());
        assertEquals(userFullDto1.getId(), usersRating.get(0).getId());

        assertEquals(1, usersRating.get(1).getRating());
        assertEquals(userFullDto2.getId(), usersRating.get(1).getId());

        assertEquals(-1, usersRating.get(2).getRating());
        assertEquals(userFullDto3.getId(), usersRating.get(2).getId());

        // events
        List<EventShortDto> eventsRating = ratingService.getEventsRating(0,10);

        assertEquals(2, eventsRating.get(0).getRating());
        assertEquals(eventFullDto1.getId(), eventsRating.get(0).getId());

        assertEquals(1, eventsRating.get(1).getRating());
        assertEquals(eventFullDto2.getId(), eventsRating.get(1).getId());

        assertEquals(0, eventsRating.get(2).getRating());
        assertEquals(eventFullDto4.getId(), eventsRating.get(2).getId());

        assertEquals(-1, eventsRating.get(3).getRating());
        assertEquals(eventFullDto3.getId(), eventsRating.get(3).getId());

        //set like to ev4 and check user1
        ratingService.setLike(eventFullDto4.getId(), userFullDto3.getId());
        assertEquals(1, eventRepository.findById(eventFullDto4.getId()).get().getRating());
        assertEquals(3, userRepository.findById(userFullDto1.getId()).get().getRating());
    }

}
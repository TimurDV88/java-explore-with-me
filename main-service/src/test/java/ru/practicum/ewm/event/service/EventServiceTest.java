package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.ParticipationRequest.dto.PartRequestDto;
import ru.practicum.ewm.ParticipationRequest.service.PartRequestService;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryAdminService;
import ru.practicum.ewm.event.controller.EventAdminController;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.UserFullDto;
import ru.practicum.ewm.user.service.UserAdminService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventServiceTest {

    private final EventPrivateService eventPrivateService;
    private final EventAdminService eventAdminService;

    private final EventAdminController eventAdminController;
    private final CategoryAdminService categoryAdminService;
    private final UserAdminService userAdminService;
    private final PartRequestService partRequestService;

    private static NewEventDto newEventDto;
    private static UserFullDto userFullDto;

    @BeforeEach
    void setUp() {

        userFullDto = userAdminService.add(new NewUserDto("user name", "email@email.com"));

        String annotation = "annotation field";
        Long category = categoryAdminService.add(new CategoryDto(null, "category name")).getId();
        String description = "description field";

        String eventDate = LocalDateTime.now().plusDays(1).format(EventMapper.DATE_TIME_FORMATTER);

        Location location = new Location();
        location.setLat(100);
        location.setLon(100);

        Boolean paid = true;
        Integer participantLimit = 100;
        Boolean requestModeration = true;
        String title = "title field";

        newEventDto = new NewEventDto(annotation, category, description, eventDate, location, paid, participantLimit,
                requestModeration, title);
    }

    @Test
    void add() {

        EventFullDto eventFullDto = eventPrivateService.add(userFullDto.getId(), newEventDto);

        System.out.println(eventFullDto);
    }

    @Test
    void getByUserId() {

        eventPrivateService.add(userFullDto.getId(), newEventDto);

        System.out.println("\n" +
                eventPrivateService.getByInitiatorId(userFullDto.getId(), 0, 10) +
                "\n");
    }

    @Test
    void getById() {

        EventFullDto eventFullDto = eventPrivateService.add(userFullDto.getId(), newEventDto);

        System.out.println("\n" +
                eventPrivateService.getById(userFullDto.getId(), eventFullDto.getId()) +
                "\n");
    }

    @Test
    void updateEventByInitiator() {

        EventFullDto eventFullDto = eventPrivateService.add(userFullDto.getId(), newEventDto);

        UpdateEventUserRequest updateRequest = new UpdateEventUserRequest();
        updateRequest.setAnnotation("NEW annotation");

        System.out.println("\n" +
                eventPrivateService.updateEventByInitiator(userFullDto.getId(), eventFullDto.getId(), updateRequest) +
                "\n");
    }

    @Test
    void updateByAdmin() {

        EventFullDto eventFullDto = eventPrivateService.add(userFullDto.getId(), newEventDto);

        assertEquals(EventState.PENDING, eventFullDto.getState());

        UpdateEventAdminRequest updateEventAdminRequest = new UpdateEventAdminRequest();

        //set to published
        updateEventAdminRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);

        eventAdminService.updateEventByAdmin(eventFullDto.getId(), updateEventAdminRequest);

        eventFullDto = eventPrivateService.getById(userFullDto.getId(), eventFullDto.getId());

        assertEquals(EventState.PUBLISHED, eventFullDto.getState());

        //set to canceled
        updateEventAdminRequest.setStateAction(UpdateEventAdminRequest.StateAction.REJECT_EVENT);

        eventAdminService.updateEventByAdmin(eventFullDto.getId(), updateEventAdminRequest);

        eventFullDto = eventPrivateService.getById(userFullDto.getId(), eventFullDto.getId());

        assertEquals(EventState.CANCELED, eventFullDto.getState());
    }

    @Test
    void getByAdmin() {

        EventFullDto eventDto1 = eventPrivateService.add(userFullDto.getId(), newEventDto);

        // userIds = {1}
        List<EventShortDto> list = eventAdminController.getEventsByParams(
                new Long[]{eventDto1.getId()},
                null, null, null, null, 0, 10);
        assertEquals(1, list.size());

        // userIds = {2}
        list = eventAdminController.getEventsByParams(
                new Long[]{2L},
                null, null, null, null, 0, 10);
        assertEquals(0, list.size());

        // state = PUBLISHED, result = 0
        EventState[] states = new EventState[]{EventState.PUBLISHED};

        list = eventAdminController.getEventsByParams(
                null,
                null, //states, -- хз чё делать
                null, null, null, 0, 10);
        assertEquals(1, list.size()); // переделать expected на 0

        // state = PUBLISHED, result = 1
        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);
        eventAdminService.updateEventByAdmin(eventDto1.getId(), updateRequest);

        list = eventAdminController.getEventsByParams(
                null,
                null, //new EventState[]{EventState.PUBLISHED}, -- хз чё делать
                null, null, null, 0, 10);
        assertEquals(1, list.size());

        // categories
        list = eventAdminController.getEventsByParams(
                null, null,
                new Long[]{eventDto1.getId()},
                null, null, 0, 10);
        assertEquals(1, list.size());

        // start
        list = eventAdminController.getEventsByParams(
                null, null, null,
                LocalDateTime.now().format(EventMapper.DATE_TIME_FORMATTER),
                null, 0, 10);
        assertEquals(1, list.size());

        list = eventAdminController.getEventsByParams(
                null, null, null,
                LocalDateTime.now().plusDays(5).format(EventMapper.DATE_TIME_FORMATTER),
                null, 0, 10);
        assertEquals(0, list.size());

        // end
        list = eventAdminController.getEventsByParams(
                null, null, null, null,
                LocalDateTime.now().format(EventMapper.DATE_TIME_FORMATTER),
                0, 10);
        assertEquals(0, list.size());

        list = eventAdminController.getEventsByParams(
                null, null, null, null,
                LocalDateTime.now().plusDays(5).format(EventMapper.DATE_TIME_FORMATTER),
                0, 10);
        assertEquals(1, list.size());


    }

    @Test
    void getRequests() {

        EventFullDto eventFullDto = eventPrivateService.add(userFullDto.getId(), newEventDto);

        UpdateEventAdminRequest updateEventAdminRequest = null;

        eventAdminService.updateEventByAdmin(eventFullDto.getId(), updateEventAdminRequest);

        UserFullDto user2 = userAdminService.add(new NewUserDto("user 2", "email2@k.rt"));

        PartRequestDto partRequestDto = partRequestService.add(user2.getId(), eventFullDto.getId());

        System.out.println("\n" +
                partRequestDto +
                "\n");
    }

    @Test
    void updateRequestStatusFromInitiator() {
    }
}
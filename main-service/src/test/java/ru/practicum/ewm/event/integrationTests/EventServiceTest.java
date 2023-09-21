package ru.practicum.ewm.event.integrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.ParticipationRequest.controller.PartRequestController;
import ru.practicum.ewm.ParticipationRequest.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.ParticipationRequest.dto.PartRequestDto;
import ru.practicum.ewm.ParticipationRequest.dto.PartRequestMapper;
import ru.practicum.ewm.ParticipationRequest.model.PartRequestState;
import ru.practicum.ewm.ParticipationRequest.model.PartRequestUpdateState;
import ru.practicum.ewm.ParticipationRequest.repository.PartRequestRepository;
import ru.practicum.ewm.category.controller.CategoryAdminController;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.event.controller.EventAdminController;
import ru.practicum.ewm.event.controller.EventPrivateController;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.service.EventPublicService;
import ru.practicum.ewm.user.controller.UserAdminController;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.UserFullDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.ewm.event.dto.UpdateEventAdminRequest.StateAction.PUBLISH_EVENT;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventServiceTest {

    private final EventPrivateController eventPrivateController;
    private final EventAdminController eventAdminController;
    private final EventPublicService eventPublicService;
    private final EventRepository eventRepository;
    private final CategoryAdminController categoryAdminController;
    private final UserAdminController userAdminController;
    private final PartRequestController partRequestController;
    private final PartRequestRepository partRequestRepository;

    private static NewEventDto newEventDto;
    private static UserFullDto userFullDto;

    @BeforeEach
    void setUp() {

        userFullDto = userAdminController.add(new NewUserDto("user_name", "email@email.com"));

        String annotation = "Assumenda mollitia hic. Nulla fugiat molestias nihil eos autem. Cupiditate rem ut. " +
                "Vel at facilis non velit iste delectus reprehenderit aperiam rerum.";

        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("category_name");
        Long category = categoryAdminController.add(newCategoryDto).getId();

        String description = "Assumenda mollitia hic. Nulla fugiat";

        String eventDate = LocalDateTime.now().plusDays(1).format(EventMapper.DATE_TIME_FORMATTER);

        Location location = new Location();
        location.setLat(100);
        location.setLon(100);

        Boolean paid = true;
        Integer participantLimit = 100;
        Boolean requestModeration = true;
        String title = "title_field";

        newEventDto = new NewEventDto(annotation, category, description, eventDate, location, paid, participantLimit,
                requestModeration, title);
    }

    /*
        EventPrivate
     */
    @Test
    void add() {

        EventFullDto eventFullDto = eventPrivateController.add(userFullDto.getId(), newEventDto);

        System.out.println(eventFullDto);
    }

    @Test
    void getByUserId() {

        eventPrivateController.add(userFullDto.getId(), newEventDto);

        System.out.println("\n" +
                eventPrivateController.getByUserId(userFullDto.getId(), 0, 10) +
                "\n");
    }

    @Test
    void getById() {

        EventFullDto eventFullDto = eventPrivateController.add(userFullDto.getId(), newEventDto);

        System.out.println("\n" +
                eventPrivateController.getById(userFullDto.getId(), eventFullDto.getId()) +
                "\n");
    }

    @Test
    void updateEventByInitiator() {

        EventFullDto eventFullDto = eventPrivateController.add(userFullDto.getId(), newEventDto);

        UpdateEventUserRequest updateRequest = new UpdateEventUserRequest();
        String newAnnotation = "NEW annotation";
        updateRequest.setAnnotation(newAnnotation);

        eventFullDto =
                eventPrivateController.updateEventByInitiator(userFullDto.getId(), eventFullDto.getId(), updateRequest);

        System.out.println("\n" +
                eventFullDto +
                "\n");

        assertEquals(newAnnotation, eventFullDto.getAnnotation());
    }

    /*
        EventAdmin
     */
    @Test
    void updateByAdmin() {

        EventFullDto eventFullDto = eventPrivateController.add(userFullDto.getId(), newEventDto);

        assertEquals(EventState.PENDING, eventFullDto.getState());

        //set new title
        String newTitle = "newTitle";

        UpdateEventAdminRequest updateEventAdminRequest = new UpdateEventAdminRequest();

        updateEventAdminRequest.setTitle(newTitle);
        eventAdminController.updateEventByAdmin(eventFullDto.getId(), updateEventAdminRequest);
        eventFullDto = eventPrivateController.getById(userFullDto.getId(), eventFullDto.getId());
        assertEquals(newTitle, eventFullDto.getTitle());

        //set to published
        updateEventAdminRequest.setStateAction(PUBLISH_EVENT);
        eventAdminController.updateEventByAdmin(eventFullDto.getId(), updateEventAdminRequest);
        eventFullDto = eventPrivateController.getById(userFullDto.getId(), eventFullDto.getId());
        assertEquals(EventState.PUBLISHED, eventFullDto.getState());

        //set to canceled
        updateEventAdminRequest.setStateAction(UpdateEventAdminRequest.StateAction.REJECT_EVENT);
        eventAdminController.updateEventByAdmin(eventFullDto.getId(), updateEventAdminRequest);
        eventFullDto = eventPrivateController.getById(userFullDto.getId(), eventFullDto.getId());
        assertEquals(EventState.CANCELED, eventFullDto.getState());
    }

    @Test
    void getByAdmin() {

        EventFullDto eventDto1 = eventPrivateController.add(userFullDto.getId(), newEventDto);

        // userIds = {1,99,199}
        List<EventFullDto> list = eventAdminController.getEventsByParams(
                new Long[]{eventDto1.getInitiator().getId(), 99L, 199L},
                null, null, null, null, 0, 10);
        assertEquals(1, list.size());

        // userIds = {2,99,199}
        list = eventAdminController.getEventsByParams(
                new Long[]{99L, 199L},
                null, null, null, null, 0, 10);
        assertEquals(0, list.size());

        // state = PUBLISHED, result = 0
        String[] states = new String[]{EventState.PUBLISHED.toString(), EventState.CANCELED.toString()};

        list = eventAdminController.getEventsByParams(
                null,
                states,
                null, null, null, 0, 10);
        assertEquals(0, list.size());

        // PUBLISH_EVENT and after state = PUBLISHED, result = 1
        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setStateAction(PUBLISH_EVENT);
        eventAdminController.updateEventByAdmin(eventDto1.getId(), updateRequest);

        list = eventAdminController.getEventsByParams(
                null,
                states,
                null, null, null, 0, 10);
        assertEquals(1, list.size());

        // categories
        list = eventAdminController.getEventsByParams(
                null, null,
                new Long[]{eventDto1.getCategory().getId(), 99L, 199L},
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

        // all params = null
        list = eventAdminController.getEventsByParams(
                null, null, null, null, null,
                0, 10);
        assertEquals(1, list.size());

        // from
        int from = 10;
        list = eventAdminController.getEventsByParams(
                null, null, null, null, null,
                from,
                10);
        assertEquals(0, list.size());
    }

    /*
        EventPrivate Participation Requests
     */
    @Test
    void getRequests() {

        EventFullDto eventFullDto = eventPrivateController.add(userFullDto.getId(), newEventDto);

        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setStateAction(PUBLISH_EVENT);
        eventAdminController.updateEventByAdmin(eventFullDto.getId(), updateRequest);

        UserFullDto user2 = userAdminController.add(new NewUserDto("user 2", "email2@k.rt"));

        partRequestController.add(user2.getId(), eventFullDto.getId());

        List<PartRequestDto> list = eventPrivateController.getRequests(userFullDto.getId(), eventFullDto.getId());

        assertEquals(1, list.size());
        assertEquals(eventFullDto.getId(), list.get(0).getEvent());
    }

    @Test
    void updateRequestStatusFromInitiator() {

        EventFullDto eventFullDto = eventPrivateController.add(userFullDto.getId(), newEventDto);

        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setStateAction(PUBLISH_EVENT);
        eventAdminController.updateEventByAdmin(eventFullDto.getId(), updateRequest);

        eventFullDto = eventPrivateController.getById(userFullDto.getId(), eventFullDto.getId());
        assertEquals(0, eventFullDto.getConfirmedRequests());

        UserFullDto user2 = userAdminController.add(new NewUserDto("user 2", "email2@k.rt"));
        UserFullDto user3 = userAdminController.add(new NewUserDto("user 3", "email3@k.rt"));

        // добавили новый запрос на участие от юзер2
        PartRequestDto partRequestDto = partRequestController.add(user2.getId(), eventFullDto.getId());
        PartRequestDto partRequestDto2 =
                PartRequestMapper.partRequestToDto(partRequestRepository.findById(partRequestDto.getId()).get());
        assertEquals(partRequestDto, partRequestDto2);
        assertEquals(PartRequestState.PENDING, partRequestDto2.getStatus());

        // добавили новый запрос на участие от юзер3
        partRequestDto = partRequestController.add(user3.getId(), eventFullDto.getId());
        PartRequestDto partRequestDto3 =
                PartRequestMapper.partRequestToDto(partRequestRepository.findById(partRequestDto.getId()).get());
        assertEquals(partRequestDto, partRequestDto3);
        assertEquals(PartRequestState.PENDING, partRequestDto3.getStatus());

        // Обновили запрос на участие от юзер2 на CONFIRMED
        EventRequestStatusUpdateRequest statusUpdateRequest = new EventRequestStatusUpdateRequest();
        statusUpdateRequest.setRequestIds(List.of(partRequestDto2.getId()));
        statusUpdateRequest.setStatus(PartRequestUpdateState.CONFIRMED);

        System.out.println("\n" +
                statusUpdateRequest
                + "\n");

        eventPrivateController.updateRequestStatusFromInitiator(userFullDto.getId(), eventFullDto.getId(),
                statusUpdateRequest);

        partRequestDto2 = PartRequestMapper
                .partRequestToDto(partRequestRepository.findById(partRequestDto2.getId()).get());
        assertEquals(PartRequestState.CONFIRMED, partRequestDto2.getStatus());

        // Обновили запрос на участие от юзер3 на REJECTED
        statusUpdateRequest.setRequestIds(List.of(partRequestDto3.getId()));
        statusUpdateRequest.setStatus(PartRequestUpdateState.REJECTED);

        System.out.println("\n" +
                statusUpdateRequest
                + "\n");

        eventPrivateController.updateRequestStatusFromInitiator(userFullDto.getId(), eventFullDto.getId(),
                statusUpdateRequest);

        partRequestDto3 = PartRequestMapper
                .partRequestToDto(partRequestRepository.findById(partRequestDto3.getId()).get());
        assertEquals(PartRequestState.REJECTED, partRequestDto3.getStatus());

        // проверили обновление confirmedRequests у события
        eventFullDto = eventPrivateController.getById(userFullDto.getId(), eventFullDto.getId());
        assertEquals(1, eventFullDto.getConfirmedRequests());
    }

    @Test
    void cancelRequest() {

        EventFullDto eventFullDto = eventPrivateController.add(userFullDto.getId(), newEventDto);

        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setStateAction(PUBLISH_EVENT);
        eventAdminController.updateEventByAdmin(eventFullDto.getId(), updateRequest);

        eventFullDto = eventPrivateController.getById(userFullDto.getId(), eventFullDto.getId());
        assertEquals(0, eventFullDto.getConfirmedRequests());

        UserFullDto user2 = userAdminController.add(new NewUserDto("user 2", "email2@k.rt"));

        PartRequestDto partRequestDto = partRequestController.add(user2.getId(), eventFullDto.getId());

        System.out.println("\nnewReq = " + partRequestDto + "\n");
        assertEquals(PartRequestState.PENDING, partRequestDto.getStatus());

        partRequestDto = partRequestController.cancelPartRequest(user2.getId(), partRequestDto.getId());

        System.out.println("\ncancelled = " + partRequestDto + "\n");
        assertEquals(PartRequestState.CANCELED, partRequestDto.getStatus());

    }

    /*
        EventPublic
     */
    @Test
    void getByParamsPublic() {

        EventFullDto eventDto1 = eventPrivateController.add(userFullDto.getId(), newEventDto);

        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setStateAction(PUBLISH_EVENT);
        eventAdminController.updateEventByAdmin(eventDto1.getId(), updateRequest);


        String uri = "test_uri";
        String ip = "test_ip";

        // text = wrong text
        String textToSearch = "something wrong";

        List<EventShortDto> list = eventPublicService.getByParameters(uri, ip,

                textToSearch,
                null, null, null, null, null,
                "EVENT_DATE", 0, 10);

        assertEquals(0, list.size());

        // text = ok text full
        textToSearch = eventDto1.getAnnotation();
        assertEquals(eventDto1.getAnnotation(), textToSearch);

        list = eventPublicService.getByParameters(uri, ip,

                textToSearch,
                null, null, null, null, null,
                "EVENT_DATE", 0, 10);

        assertEquals(1, list.size());

        // text = ok text long
        textToSearch = "Assumenda mollitia hic. Nulla fugiat molestias nihil eos autem. Cupiditate rem ut. " +
                "Vel at facilis non velit iste delectus reprehenderit aperiam rerum.";
        assert (eventDto1.getDescription().toLowerCase().contains(textToSearch.toLowerCase())
                || eventDto1.getAnnotation().toLowerCase().contains(textToSearch.toLowerCase()));

        list = eventPublicService.getByParameters(uri, ip,

                textToSearch,
                null, null, null, null, null,
                "EVENT_DATE", 0, 10);

        assertEquals(1, list.size());

        // categories
        list = eventPublicService.getByParameters(uri, ip,

                null,
                new Long[]{eventDto1.getCategory().getId(), 99L, 199L},
                null, null, null, null,
                "EVENT_DATE", 0, 10);

        assertEquals(1, list.size());

        // paid
        list = eventPublicService.getByParameters(uri, ip,

                null, null,
                true,
                null, null, null,
                "EVENT_DATE", 0, 10);

        assertEquals(1, list.size());

        list = eventPublicService.getByParameters(uri, ip,

                null, null,
                false,
                null, null, null,
                "EVENT_DATE", 0, 10);

        assertEquals(0, list.size());




    }

    @Test
    void getByIdPublic() {

        EventFullDto eventDto1 = eventPrivateController.add(userFullDto.getId(), newEventDto);

        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setStateAction(PUBLISH_EVENT);
        eventAdminController.updateEventByAdmin(eventDto1.getId(), updateRequest);

        // check event views
        assertEquals(0, eventDto1.getViews());

        String uri = "test_uri_for_single_event";
        String ip = "test_ip_for_single_event";

        eventPublicService.getById(eventDto1.getId(), uri, ip);

        Event event = eventRepository.findById(eventDto1.getId()).get();
        assertEquals(1, event.getViews());
    }
}
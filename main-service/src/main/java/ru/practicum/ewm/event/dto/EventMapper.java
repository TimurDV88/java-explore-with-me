package ru.practicum.ewm.event.dto;

import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventMapper {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static <T> void setIfNotNull(final Consumer<T> targetConsumer, final T value) {

        if (value != null) {

            targetConsumer.accept(value);
        }
    }

    public static Event newEventToModel(NewEventDto newEventDto, Category category, User initiator) {

        Event event = new Event();

        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), DATE_TIME_FORMATTER));
        event.setInitiator(initiator);
        event.setLocation(newEventDto.getLocation());
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setTitle(newEventDto.getTitle());

        return event;
    }

    public static EventFullDto eventToFullDto(Event event) {

        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.categoryToDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn().format(EventMapper.DATE_TIME_FORMATTER),
                event.getDescription(),
                event.getEventDate().format(EventMapper.DATE_TIME_FORMATTER),
                UserMapper.userToShortDto(event.getInitiator()),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                EventState.valueOf(event.getState()),
                event.getTitle(),
                event.getViews()
        );
    }

    public static List<EventFullDto> eventToFullDto(Iterable<Event> events) {

        List<EventFullDto> listToReturn = new ArrayList<>();

        for (Event event : events) {
            listToReturn.add(eventToFullDto(event));
        }

        return listToReturn;
    }

    public static EventShortDto eventToShortDto(Event event) {

        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.categoryToDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate().format(EventMapper.DATE_TIME_FORMATTER),
                UserMapper.userToShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                event.getViews()
        );
    }

    public static List<EventShortDto> eventToShortDto(Iterable<Event> events) {

        List<EventShortDto> listToReturn = new ArrayList<>();

        for (Event event : events) {
            listToReturn.add(eventToShortDto(event));
        }

        return listToReturn;
    }
}

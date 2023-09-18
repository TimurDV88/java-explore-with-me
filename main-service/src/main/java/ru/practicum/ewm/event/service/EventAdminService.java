package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.error.exception.ConflictOnRequestException;
import ru.practicum.ewm.error.exception.IncorrectRequestException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventAdminService {

    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    public List<EventShortDto> getByParams(Long[] users,
                                           EventState[] states,
                                           Long[] categories,
                                           String rangeStart,
                                           String rangeEnd,
                                           int from,
                                           int size) {

        log.info("-- Возвращение событий с параметрами");

        PageRequest pageRequest;

        if (size > 0 && from >= 0) {
            int page = from / size;
            pageRequest = PageRequest.of(page, size, Sort.by("eventDate").ascending());
        } else {
            throw new IncorrectRequestException("- Размер страницы должен быть > 0, 'from' должен быть >= 0");
        }

        // пользователи
        BooleanExpression byUsers;
        if (users != null) {
            byUsers = QEvent.event.initiator.id.in(users);
        } else {
            byUsers = QEvent.event.isNotNull();
        }

        // статусы
        BooleanExpression byStates;
        if (states != null) {
            byStates = QEvent.event.state.in(Arrays.asList(states));
        } else {
            byStates = QEvent.event.isNotNull();
        }

        // категории
        BooleanExpression byCategory;
        if (categories != null) {
            byCategory = QEvent.event.category.id.in(categories);
        } else {
            byCategory = QEvent.event.isNotNull();
        }

        // блок старт
        BooleanExpression byStart;
        LocalDateTime rangeStartDate;
        if (rangeStart != null) {
            rangeStartDate = LocalDateTime.parse(rangeStart, EventMapper.DATE_TIME_FORMATTER);
        } else {
            rangeStartDate = LocalDateTime.now();
        }
        byStart = QEvent.event.eventDate.after(rangeStartDate);

        // блок энд
        BooleanExpression byEnd;
        if (rangeEnd != null) {
            byEnd = QEvent.event.eventDate.before(LocalDateTime.parse(rangeEnd, EventMapper.DATE_TIME_FORMATTER));
        } else {
            byEnd = QEvent.event.isNotNull();
        }

        // запрос в бд через QDSL
        Iterable<Event> foundEvents = eventRepository.findAll(byUsers

                        .and(byStates)
                        .and(byCategory)
                        .and(byStart)
                        .and(byEnd));

                //pageRequest);

        // маппинг для возврата полученного списка
        List<EventShortDto> listToReturn = EventMapper.eventToShortDto(foundEvents);

        log.info("-- Список событий возвращён, его размер: {}", listToReturn.size());

        return listToReturn;
    }

    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {

        log.info("-- Обновление события id={} от admin", eventId);

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("- Событие с id=" + eventId + " не найдено"));

        //блок проверок
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictOnRequestException(
                    "- Событие не может быть раньше, чем через час от текущего момента ");
        }
        //конец блока проверок

        EventMapper.setIfNotNull(event::setAnnotation, updateRequest.getAnnotation());

        if (updateRequest.getCategory() != null) {
            Long categoryId = updateRequest.getCategory();
            Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                    new NotFoundException("- Категория с id=" + categoryId + " не найдена"));
            event.setCategory(category);
        }

        EventMapper.setIfNotNull(event::setDescription, updateRequest.getDescription());
        EventMapper.setIfNotNull(event::setEventDate, updateRequest.getEventDate());
        EventMapper.setIfNotNull(event::setLocation, updateRequest.getLocation());
        EventMapper.setIfNotNull(event::setPaid, updateRequest.getPaid());
        EventMapper.setIfNotNull(event::setParticipantLimit, updateRequest.getParticipantLimit());
        EventMapper.setIfNotNull(event::setRequestModeration, updateRequest.getRequestModeration());

        if (updateRequest.getStateAction() != null) {
            if (updateRequest.getStateAction().equals(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (updateRequest.getStateAction().equals(UpdateEventAdminRequest.StateAction.REJECT_EVENT)) {
                event.setState(EventState.CANCELED);
            }
        }

        EventMapper.setIfNotNull(event::setTitle, updateRequest.getTitle());

        EventFullDto eventFullDto = EventMapper.eventToFullDto(eventRepository.save(event));

        log.info("-- Событие id={} от admin обновлено", eventId);

        return eventFullDto;
    }
}

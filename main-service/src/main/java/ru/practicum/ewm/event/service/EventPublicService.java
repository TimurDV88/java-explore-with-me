package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.error.exception.IncorrectRequestException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventPublicService {

    private final EventRepository eventRepository;

    public List<EventShortDto> getByParameters(String text,
                                               Integer[] categories,
                                               Boolean paid,
                                               String rangeStart,
                                               String rangeEnd,
                                               Boolean onlyAvailable,
                                               String sort,
                                               int from,
                                               int size) {

        log.info("-- Возвращение событиий по условиям поиска");

        // блок пагинации
        PageRequest pageRequest;

        if (!sort.equalsIgnoreCase("EVENT_DATE")
                && !sort.equalsIgnoreCase("VIEWS")) {
            throw new IncorrectRequestException("- sort должен быть EVENT_DATE или VIEWS");
        }

        if (size > 0 && from >= 0) {
            int page = from / size;
            pageRequest = PageRequest.of(page, size, Sort.by(sort).ascending());
        } else {
            throw new IncorrectRequestException("- Размер страницы должен быть > 0, 'from' должен быть >= 0");
        }

        // text
        BooleanExpression byText =
                QEvent.event.annotation.toLowerCase().eq(text.toLowerCase())
                        .or(QEvent.event.description.toLowerCase().eq(text.toLowerCase()));

        // категории
        BooleanExpression byCategory;
        if (categories != null) {
            byCategory = QEvent.event.category.id.in(categories);
        } else {
            byCategory = QEvent.event.isNotNull();
        }

        // платность
        BooleanExpression byPaid = QEvent.event.paid.eq(paid);

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
            byEnd = QEvent.event.eventDate.after(LocalDateTime.parse(rangeEnd, EventMapper.DATE_TIME_FORMATTER));
        } else {
            byEnd = QEvent.event.isNotNull();
        }

        // блок доступность
        BooleanExpression byAvailable;
        if (onlyAvailable) {
            byAvailable = QEvent.event.confirmedRequests.lt(QEvent.event.participantLimit);
        } else {
            byAvailable = QEvent.event.isNotNull();
        }

        // блок проверки опубликованности
        BooleanExpression byState = QEvent.event.state.eq(EventState.PUBLISHED);

        // запрос в бд через QDSL
        Iterable<Event> foundEvents = eventRepository.findAll(byText

                        .and(byCategory)
                        .and(byPaid)
                        .and(byStart)
                        .and(byEnd)
                        .and(byAvailable)
                        .and(byState),

                pageRequest);

        // отметка просмотров событий в бд
        List<Long> foundIds = new ArrayList<>();
        for (Event event : foundEvents) {
            foundIds.add(event.getId());
        }
        eventRepository.updateViewsByIds(foundIds);

        // маппинг для возврата полученного списка
        List<EventShortDto> listToReturn = EventMapper.eventToShortDto(foundEvents);

        log.info("--- Список событий возвращен, его размер: {}", listToReturn.size());

        return listToReturn;
    }

    public EventFullDto getById(Long eventId) {

        log.info("-- Возвращение события id={}", eventId);

        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED).orElseThrow(() ->
                new NotFoundException("- Событие с id=" + eventId + " не найдено или не опубликовано"));

        // отметка просмотров события в бд
        eventRepository.updateViewsByIds(List.of(eventId));

        EventFullDto eventFullDto = EventMapper.eventToFullDto(event);

        log.info("-- Событие с id={} возвращёно", eventId);

        return eventFullDto;
    }

}

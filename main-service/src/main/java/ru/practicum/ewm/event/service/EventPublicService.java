package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatClientService;
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

    @Value("${app-name}")
    private String appName;

    private final StatClientService statClientService;

    private final EventRepository eventRepository;

    public List<EventShortDto> getByParameters(String uri, String ip,

                                               String text,
                                               Long[] categories,
                                               Boolean paid,
                                               String rangeStart,
                                               String rangeEnd,
                                               Boolean onlyAvailable,
                                               String sort,
                                               int from,
                                               int size) {

        log.info("-- Возвращение событий с параметрами (Public): " +
                        "text={}, categories={}, paid={}, start={}, end={}, onlyAvailable={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size);

        // блок проверок:

        // start end
        LocalDateTime rangeStartDate;
        LocalDateTime rangeEndDate;
        if (rangeStart != null && rangeEnd != null) {
            rangeStartDate = LocalDateTime.parse(rangeStart, EventMapper.DATE_TIME_FORMATTER);
            rangeEndDate = LocalDateTime.parse(rangeEnd, EventMapper.DATE_TIME_FORMATTER);
            if (rangeEndDate.isBefore(rangeStartDate)) {
                throw new IncorrectRequestException("- start должен быть раньше end");
            }
        }

        // sort
        if (sort.equalsIgnoreCase("EVENT_DATE")) {
            sort = "eventDate";
        } else if (sort.equalsIgnoreCase("VIEWS")) {
            sort = "views";
        } else {
            throw new IncorrectRequestException("- sort должен быть EVENT_DATE или VIEWS");
        }
        // конец блока проверок

        // отправляем запись о запросе в сервис статистики
        statClientService.addStatRecord(appName, uri, ip);

        // блок пагинации
        PageRequest pageRequest;

        if (size > 0 && from >= 0) {
            int page = from / size;
            pageRequest = PageRequest.of(page, size, Sort.by(sort).ascending());
        } else {
            throw new IncorrectRequestException("- Размер страницы должен быть > 0, 'from' должен быть >= 0");
        }

        // Блок проверки опубликованности
        BooleanExpression byState = QEvent.event.state.eq(EventState.PUBLISHED.toString());

        // Блок поиска:

        // text
        BooleanExpression byText;
        if (text != null) {
            byText = QEvent.event.annotation.containsIgnoreCase(text)
                    .or(QEvent.event.description.containsIgnoreCase(text));
        } else {
            byText = null;
        }

        // категории
        BooleanExpression byCategory;
        if (categories != null) {
            byCategory = QEvent.event.category.id.in(categories);
        } else {
            byCategory = null;
        }

        // платность
        BooleanExpression byPaid;
        if (paid != null) {
            byPaid = QEvent.event.paid.eq(paid);
        } else {
            byPaid = null;
        }

        // старт
        BooleanExpression byStart;
        if (rangeStart != null) {
            rangeStartDate = LocalDateTime.parse(rangeStart, EventMapper.DATE_TIME_FORMATTER);
        } else {
            rangeStartDate = LocalDateTime.now();
        }
        byStart = QEvent.event.eventDate.after(rangeStartDate);

        // энд
        BooleanExpression byEnd;
        if (rangeEnd != null) {
            byEnd = QEvent.event.eventDate.before(LocalDateTime.parse(rangeEnd, EventMapper.DATE_TIME_FORMATTER));
        } else {
            byEnd = null;
        }

        // доступность
        BooleanExpression byAvailable;
        if (onlyAvailable != null && onlyAvailable) {
            byAvailable = QEvent.event.confirmedRequests.lt(QEvent.event.participantLimit);
        } else {
            byAvailable = null;
        }

        // запрос в бд через QDSL
        Iterable<Event> foundEvents = eventRepository.findAll(byState

                        .and(byText)
                        .and(byCategory)
                        .and(byPaid)
                        .and(byStart)
                        .and(byEnd)
                        .and(byAvailable),

                pageRequest);

        // маппинг для возврата полученного списка
        List<EventShortDto> listToReturn = EventMapper.eventToShortDto(foundEvents);

        log.info("-- Список событий (Public) возвращен, его размер: {}", listToReturn.size());

        return listToReturn;
    }

    public EventFullDto getById(Long eventId, String uri, String ip) {

        log.info("-- Возвращение события id={} (Public)", eventId);

        // отправляем запись о запросе в сервис статистики
        statClientService.addStatRecord(appName, uri, ip);

        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED.toString()).orElseThrow(() ->
                new NotFoundException("- Событие с id=" + eventId + " не найдено или не опубликовано (Public)"));

        // отметка просмотров события в бд
        Integer views = statClientService.getViewsByUri(appName, uri);
        event.setViews(views);

        EventFullDto eventFullDto = EventMapper.eventToFullDto(eventRepository.save(event)); //event);

        log.info("-- Событие с id={} возвращёно (Public)", eventId);

        return eventFullDto;
    }

}

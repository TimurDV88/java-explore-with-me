package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.ParticipationRequest.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.ParticipationRequest.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.ParticipationRequest.dto.PartRequestDto;
import ru.practicum.ewm.ParticipationRequest.dto.PartRequestMapper;
import ru.practicum.ewm.ParticipationRequest.model.PartRequestState;
import ru.practicum.ewm.ParticipationRequest.model.PartRequestUpdateState;
import ru.practicum.ewm.ParticipationRequest.model.ParticipationRequest;
import ru.practicum.ewm.ParticipationRequest.repository.PartRequestRepository;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.error.exception.ConflictOnRequestException;
import ru.practicum.ewm.error.exception.IncorrectRequestException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventPrivateService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PartRequestRepository partRequestRepository;


    public EventFullDto add(Long initiatorId, NewEventDto newEventDto) {

        log.info("-- Добавление события от пользователя id={}: {}", initiatorId, newEventDto);

        //блок проверок
        LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(), EventMapper.DATE_TIME_FORMATTER);

        if (eventDate.isBefore(LocalDateTime.now())) {
            throw new IncorrectRequestException("- Время события должно быть позже текущего");
        }
        // конец блока проверок

        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() ->
                new NotFoundException("- Категория №" + newEventDto.getCategory() + " не найдена в базе"));

        User initiator = userRepository.findById(initiatorId).orElseThrow(() ->
                new NotFoundException("- Пользователь с id=" + initiatorId + " не найден"));

        Event event = EventMapper.newEventToModel(newEventDto, category, initiator);

        event.setConfirmedRequests(0);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setViews(0);

        EventFullDto eventFullDto = EventMapper.eventToFullDto(eventRepository.save(event));

        log.info("-- Событие от пользователя id={} добавлено: {}", initiatorId, eventFullDto);

        return eventFullDto;
    }

    public List<EventShortDto> getByInitiatorId(Long initiatorId, int from, int size) {

        log.info("-- Возвращение всех событий от пользователя id={}", initiatorId);

        PageRequest pageRequest;

        if (size > 0 && from >= 0) {
            int page = from / size;
            pageRequest = PageRequest.of(page, size, Sort.by("eventDate").ascending());
        } else {
            throw new IncorrectRequestException("- Размер страницы должен быть > 0, 'from' должен быть >= 0");
        }

        List<EventShortDto> listToReturn = EventMapper.eventToShortDto(
                eventRepository.findByInitiatorId(initiatorId, pageRequest));

        log.info("-- Список событий от пользователя id={} возвращён, его размер: {}", initiatorId, listToReturn.size());

        return listToReturn;
    }

    public EventFullDto getById(Long initiatorId, Long eventId) {

        log.info("-- Возвращение события id={} от пользователя id={}", eventId, initiatorId);

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("- Событие с id=" + eventId + " не найдено"));

        //блок проверок
        if (!event.getInitiator().getId().equals(initiatorId)) {
            throw new IncorrectRequestException("- Пользователь не является инициатором события id=" + eventId);
        }
        //конец блока проверок

        EventFullDto eventFullDto = EventMapper.eventToFullDto(event);

        log.info("-- Событие с id={} возвращёно", eventId);

        return eventFullDto;
    }

    public EventFullDto updateEventByInitiator(Long initiatorId, Long eventId, UpdateEventUserRequest updateRequest) {

        log.info("-- Обновление события id={} от пользователя id={}", eventId, initiatorId);

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("- Событие с id=" + eventId + " не найдено"));

        //блок проверок
        if (!event.getInitiator().getId().equals(initiatorId)) {
            throw new IncorrectRequestException("- Пользователь не является инициатором события id=" + eventId);
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictOnRequestException("- Нельзя изенять событие со статусом " + EventState.PUBLISHED);
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictOnRequestException(
                    "- Событие не может быть раньше, чем через два часа от текущего момента ");
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

        if (updateRequest.getStateAction() != null
                && (updateRequest.getStateAction().equals(UpdateEventUserRequest.StateAction.CANCEL_REVIEW))) {
            event.setState(EventState.CANCELED);
        }

        EventMapper.setIfNotNull(event::setTitle, updateRequest.getTitle());

        EventFullDto eventFullDto = EventMapper.eventToFullDto(eventRepository.save(event));

        log.info("-- Событие id={} от пользователя id={} обновлено", eventId, initiatorId);

        return eventFullDto;
    }

    public List<PartRequestDto> getRequests(Long initiatorId, Long eventId) {

        log.info("-- Возвращение списка запросов на участие в событии id={}", eventId);

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("- Событие с id=" + eventId + " не найдено"));

        //блок проверок
        if (!event.getInitiator().getId().equals(initiatorId)) {
            throw new IncorrectRequestException("- Пользователь не является инициатором события id=" + eventId);
        }
        //конец блока проверок

        List<PartRequestDto> listToReturn = partRequestRepository.findByEventId(eventId)
                .stream()
                .map(PartRequestMapper::partRequestToDto)
                .collect(Collectors.toList());

        log.info("-- Список запросов на участие в событи id={} возвращён, его размер: {}",
                eventId, listToReturn.size());

        return listToReturn;
    }

    public EventRequestStatusUpdateResult updateRequestStatusFromInitiator(

            Long initiatorId,
            Long eventId,
            EventRequestStatusUpdateRequest statusUpdateRequest) {

        List<Long> ids = statusUpdateRequest.getRequestIds();

        log.info("-- Обновление статусов запросов на участие в событии id={}: {}",
                eventId, ids.toString());

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("- Событие с id=" + eventId + " не найдено"));

        Integer numberOfConfirmedRequests;
        if (event.getConfirmedRequests() != null) {
            numberOfConfirmedRequests = event.getConfirmedRequests();
        } else {
            numberOfConfirmedRequests = 0;
        }

        //блок проверок
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new IncorrectRequestException("- Подтверждение заявок не требуется событию с id=" + eventId);
        }

        if (!event.getInitiator().getId().equals(initiatorId)) {
            throw new IncorrectRequestException("- Пользователь не является инициатором события id=" + eventId);
        }

        if (numberOfConfirmedRequests >= event.getParticipantLimit()) {
            throw new ConflictOnRequestException("- Достигнут лимит запросов на участие в событии");
        }
        //конец блока проверок

        List<ParticipationRequest> requests = partRequestRepository.findByIdIn(ids);
        PartRequestUpdateState stateToSet = statusUpdateRequest.getPartRequestState();
        List<Long> confirmedIds = new ArrayList<>();
        List<Long> rejectedIds = new ArrayList<>();

        for (ParticipationRequest request : requests) {

            Long requestId = request.getId();

            if (!request.getState().equals(PartRequestState.WAITING)) {
                log.info("-- Запрос на участие с id={} не имеет статус {}",
                        requestId, PartRequestState.WAITING);
                continue;
            }

            if (numberOfConfirmedRequests >= event.getParticipantLimit()
                    || stateToSet.equals(PartRequestUpdateState.REJECTED)) {

                request.setState(PartRequestState.REJECTED);
                rejectedIds.add(requestId);
                continue;
            }

            if (!request.getEventId().equals(eventId)) {
                log.info("-- Запрос на участие с id={} не относится к собыию с id={}",
                        requestId, eventId);
                continue;
            }

            request.setState(PartRequestState.CONFIRMED);
            confirmedIds.add(requestId);
            numberOfConfirmedRequests++;
        }

        partRequestRepository.setStatus(confirmedIds, PartRequestState.CONFIRMED);
        partRequestRepository.setStatus(rejectedIds, PartRequestState.REJECTED);

        eventRepository.updateConfirmedRequests(eventId, numberOfConfirmedRequests);
        log.info("-- Число подтвержденных запросов на участие в событии с id={} обновлено: {}",
                eventId, numberOfConfirmedRequests);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(
                PartRequestMapper.partRequestToDto(
                        partRequestRepository.findByEventIdAndState(eventId, PartRequestState.CONFIRMED)),
                PartRequestMapper.partRequestToDto(
                        partRequestRepository.findByEventIdAndState(eventId, PartRequestState.REJECTED))
        );

        log.info("-- Статусы запросов на участие в событии id={} обновлены, одобрено: {}, отклонено: {}",
                eventId, result.getConfirmedRequests().size(), result.getRejectedRequests().size());

        return result;
    }


}

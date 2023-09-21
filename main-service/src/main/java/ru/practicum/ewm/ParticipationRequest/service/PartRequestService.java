package ru.practicum.ewm.ParticipationRequest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.ParticipationRequest.dto.PartRequestDto;
import ru.practicum.ewm.ParticipationRequest.dto.PartRequestMapper;
import ru.practicum.ewm.ParticipationRequest.model.PartRequestState;
import ru.practicum.ewm.ParticipationRequest.model.ParticipationRequest;
import ru.practicum.ewm.ParticipationRequest.repository.PartRequestRepository;
import ru.practicum.ewm.error.exception.ConflictOnRequestException;
import ru.practicum.ewm.error.exception.IncorrectRequestException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartRequestService {

    private final PartRequestRepository partRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public PartRequestDto add(Long requesterId, Long eventId) {

        log.info("-- Добавление запроса от пользователя id={} на участие в событии id={}",
                requesterId, eventId);

        String currentTimeString = LocalDateTime.now().format(EventMapper.DATE_TIME_FORMATTER);

        if (!userRepository.existsById(requesterId)) {
            throw new NotFoundException("- Пользователь с id=" + requesterId + " не найден");
        }

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("- Событие с id=" + eventId + " не найдено"));

        //блок проверок
        if (partRequestRepository.existsByRequesterAndEvent(requesterId, eventId)) {
            throw new ConflictOnRequestException("- Запрос пользователя id=" + requesterId +
                    " на участие в этом событии уже существует");
        }

        if (event.getInitiator().getId().equals(requesterId)) {
            throw new ConflictOnRequestException("- Пользователь не может создавать запрос на участие " +
                    "в своем событии");
        }

        if (!event.getState().equals(EventState.PUBLISHED.toString())) {
            throw new ConflictOnRequestException("- Нельзя участвовать в неопубликованном событии");
        }

        Integer confRequests = event.getConfirmedRequests();
        Integer partLimit = event.getParticipantLimit();

        if (confRequests != null && partLimit != null && partLimit != 0
                && confRequests >= partLimit) {

            throw new ConflictOnRequestException("- Достигнут лимит запросов на участие в событии");
        }
        // конец блока проверок

        // создаём новый запрос
        ParticipationRequest participationRequest = new ParticipationRequest();

        participationRequest.setCreated(LocalDateTime.parse(currentTimeString, EventMapper.DATE_TIME_FORMATTER));
        participationRequest.setEvent(eventId);
        participationRequest.setRequester(requesterId);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(PartRequestState.CONFIRMED.toString());
            eventRepository.updateConfirmedRequestsById(eventId);
        } else {
            participationRequest.setStatus(PartRequestState.PENDING.toString());
        }

        PartRequestDto partRequestDto =
                PartRequestMapper.partRequestToDto(partRequestRepository.save(participationRequest));

        log.info("-- Запрос от пользователя id={} на участие в событии id={} добавлен: {}",
                requesterId, eventId, partRequestDto);

        return partRequestDto;
    }

    public List<PartRequestDto> getByRequesterId(Long requesterId) {

        log.info("-- Возвращение запросов на участие в событиях от пользователя id={}",
                requesterId);

        if (!userRepository.existsById(requesterId)) {
            throw new NotFoundException("- Пользователь с id=" + requesterId + " не найден");
        }

        List<PartRequestDto> listToReturn = partRequestRepository.findByRequester(requesterId)
                .stream()
                .map(PartRequestMapper::partRequestToDto)
                .collect(Collectors.toList());

        log.info("-- Список запросов на участие в событиях от пользователя id={} возвращён, его размер: {}",
                requesterId, listToReturn.size());

        return listToReturn;
    }

    public PartRequestDto cancelPartRequest(Long requesterId, Long requestId) {

        log.info("-- Отмена запроса id={} от пользователя id={}",
                requestId, requesterId);

        ParticipationRequest request = partRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("- Запрос с id=" + requestId + " не найден"));

        //блок проверок
        if (!userRepository.existsById(requesterId)) {
            throw new NotFoundException("- Пользователь с id=" + requesterId + " не найден");
        }

        if (!request.getRequester().equals(requesterId)) {
            throw new IncorrectRequestException("- Пользователь с id=" + requesterId +
                    " не создавал запрос c id=" + requestId);
        }
        //конец блока проверок

        request.setStatus(PartRequestState.CANCELED.toString());

        PartRequestDto requestDto = PartRequestMapper.partRequestToDto(partRequestRepository.save(request));

        log.info("-- Статус запроса id={} от пользователя id={} изменен на {}",
                requestId, requesterId, PartRequestState.CANCELED);

        return requestDto;
    }

}

package ru.practicum.ewm.rating.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.participationRequest.model.PartRequestState;
import ru.practicum.ewm.participationRequest.model.ParticipationRequest;
import ru.practicum.ewm.participationRequest.repository.PartRequestRepository;
import ru.practicum.ewm.error.exception.IncorrectRequestException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.rating.dto.RatingMapper;
import ru.practicum.ewm.rating.model.Rating;
import ru.practicum.ewm.rating.repository.RatingRepository;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Optional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private final PartRequestRepository partRequestRepository;

    @Transactional
    public RatingDto setLike(Long eventId, Long userId) {

        log.info("-- Добавление лайка от пользователя id={} событию id={}", userId, eventId);

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("- Событие с id=" + eventId + " не найдено"));

        User initiator = event.getInitiator();

        Optional<ParticipationRequest> partRequest = partRequestRepository.findByEventAndRequester(eventId, userId);

        //блок проверок
        if (partRequest.isEmpty()) {
            throw new IncorrectRequestException("- Запрос пользователя на участие в событии отсутствует");
        }
        if (!partRequest.get().getStatus().
                equals(PartRequestState.CONFIRMED.toString())) {
            throw new IncorrectRequestException("- Запрос пользователя на участие в событии не подтвержден");
        }
        //конец блока проверок

        Optional<Rating> currentRatingOptional = ratingRepository.findByEventIdAndUserId(eventId, userId);


        // если пользователь уже оценивал событие
        if (currentRatingOptional.isPresent()) {

            Rating currentRating = currentRatingOptional.get();

            // если лайк от этого пользователя уже был, то повторное проставление убирает текущий лайк
            if (currentRating.getRating() == 1) {
                ratingRepository.deleteById(currentRating.getId());
                event.setRating(event.getRating() - 1);
                initiator.setRating(initiator.getRating() - 1);
                log.info("-- Существующий лайк удалён при повторном добавлении лайка событию");
                return null;
            } else if (currentRating.getRating() == -1) {
                currentRating.setRating(1);
                currentRating = ratingRepository.save(currentRating);
                event.setRating(event.getRating() + 2);
                initiator.setRating(initiator.getRating() + 2);
                log.info("-- Существующий дизлайк удалён, новый лайк добавлен");
                return RatingMapper.ratingToDto(currentRating);
            }
        }

        // если пользователь ещё не оценивал событие
        Rating newRating = new Rating();
        newRating.setEventId(eventId);
        newRating.setUserId(userId);
        newRating.setRating(1);
        newRating = ratingRepository.save(newRating);
        event.setRating(event.getRating() + 1);
        initiator.setRating(initiator.getRating() + 1);

        log.info("-- Новый лайк добавлен");

        return RatingMapper.ratingToDto(newRating);
    }

    @Transactional
    public RatingDto setDislike(Long eventId, Long userId) {

        log.info("-- Добавление дизлайка от пользователя id={} событию id={}", userId, eventId);

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("- Событие с id=" + eventId + " не найдено"));

        User initiator = event.getInitiator();

        Optional<ParticipationRequest> partRequest = partRequestRepository.findByEventAndRequester(eventId, userId);

        //блок проверок
        if (partRequest.isEmpty()) {
            throw new IncorrectRequestException("- Запрос пользователя на участие в событии отсутствует");
        }
        if (!partRequest.get().getStatus().
                equals(PartRequestState.CONFIRMED.toString())) {
            throw new IncorrectRequestException("- Запрос пользователя на участие в событии не подтвержден");
        }
        //конец блока проверок

        Optional<Rating> currentRatingOptional = ratingRepository.findByEventIdAndUserId(eventId, userId);

        // если пользователь уже оценивал событие
        if (currentRatingOptional.isPresent()) {

            Rating currentRating = currentRatingOptional.get();

            // если дизлайк от этого пользователя уже был, то повторное проставление убирает текущий дизлайк
            if (currentRating.getRating() == -1) {
                ratingRepository.deleteById(currentRating.getId());
                event.setRating(event.getRating() + 1);
                initiator.setRating(initiator.getRating() + 1);
                log.info("-- Существующий дизлайк удалён при повторном добавлении дизлайка событию");
                return null;
            } else if (currentRating.getRating() == 1) {
                currentRating.setRating(-1);
                currentRating = ratingRepository.save(currentRating);
                event.setRating(event.getRating() - 2);
                initiator.setRating(initiator.getRating() - 2);
                log.info("-- Существующий лайк удалён, новый дизлайк добавлен");
                return RatingMapper.ratingToDto(currentRating);
            }
        }

        // если пользователь ещё не оценивал событие
        Rating newRating = new Rating();
        newRating.setEventId(eventId);
        newRating.setUserId(userId);
        newRating.setRating(-1);
        newRating = ratingRepository.save(newRating);
        event.setRating(event.getRating() - 1);
        initiator.setRating(initiator.getRating() - 1);

        log.info("-- Новый дизлайк добавлен");

        return RatingMapper.ratingToDto(newRating);
    }

    /*
        Метод пагинации
     */
    private PageRequest getPageRequest(int from, int size) {

        if (size > 0 && from >= 0) {
            int page = from / size;
            return PageRequest.of(page, size, Sort.by("rating").descending());
        } else {
            throw new IncorrectRequestException("- Размер страницы должен быть > 0, 'from' должен быть >= 0");
        }
    }

    public List<UserShortDto> getUsersRating(int from, int size) {

        log.info("-- Возвращение рейтинга пользователей с параметрами: from={}, size={}", from, size);

        PageRequest pageRequest = getPageRequest(from, size);

        Iterable<User> foundUsers = userRepository.findAll(pageRequest);

        List<UserShortDto> listToReturn = UserMapper.userToShortDto(foundUsers);

        log.info("-- Рейтинг пользователей возвращен, его размер: {}", listToReturn.size());

        return listToReturn;
    }

    public List<EventShortDto> getEventsRating(int from, int size) {

        log.info("-- Возвращение рейтинга событий с параметрами: from={}, size={}", from, size);

        PageRequest pageRequest = getPageRequest(from, size);

        Iterable<Event> foundEvents = eventRepository.findAll(pageRequest);

        List<EventShortDto> listToReturn = EventMapper.eventToShortDto(foundEvents);

        log.info("-- Рейтинг событий возвращен, его размер: {}", listToReturn.size());

        return listToReturn;
    }
}

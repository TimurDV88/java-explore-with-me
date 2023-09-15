package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Page<Event> findAllByInitiatorId(Long initiatorId, PageRequest pageRequest);

    @Query("UPDATE Event " +
            "SET confirmedRequests = :confirmedRequests " +
            "WHERE id = :eventId")
    void updateConfirmedRequests(Long eventId, Integer confirmedRequests);

    Optional<Event> findByIdAndState(Long eventId, EventState state);
}

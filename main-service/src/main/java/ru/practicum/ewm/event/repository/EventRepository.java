package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.initiator.id = :initiatorId")
    Page<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findByIdAndState(Long eventId, String state);

    List<Event> findAllByIdIn(Long[] ids);

    boolean existsByCategoryId(Long categoryId);

    @Modifying
    @Query("UPDATE Event " +
            "SET confirmed_requests = confirmed_requests + 1 " +
            "WHERE id = :id")
    void updateConfirmedRequestsById(Long id);
}

package ru.practicum.ewm.ParticipationRequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.ParticipationRequest.model.PartRequestState;
import ru.practicum.ewm.ParticipationRequest.model.ParticipationRequest;

import java.util.List;

public interface PartRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    boolean existsByEventId(Long eventId);

    List<ParticipationRequest> findByRequesterId(Long requesterId);

    List<ParticipationRequest> findByEventId(Long eventId);

    List<ParticipationRequest> findByIdIn(List<Long> ids);

    @Query("UPDATE ParticipationRequest " +
            "SET state = :state " +
            "WHERE id IN (:ids)")
    void setStatus(List<Long> ids, PartRequestState state);

    List<ParticipationRequest> findByEventIdAndState(Long eventId, PartRequestState state);
}

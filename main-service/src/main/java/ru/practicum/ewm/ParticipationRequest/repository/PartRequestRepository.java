package ru.practicum.ewm.ParticipationRequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.ParticipationRequest.model.ParticipationRequest;

import java.util.List;

public interface PartRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<ParticipationRequest> findByRequesterId(Long requesterId);

    List<ParticipationRequest> findByEventId(Long eventId);

    List<ParticipationRequest> findByIdIn(List<Long> ids);

    @Modifying
    @Query(value = "UPDATE ParticipationRequest " +
            "SET state = :state " +
            "WHERE id IN (:ids)")
    void setStatus(List<Long> ids, String state);

    List<ParticipationRequest> findByEventIdAndIdIn(Long eventId, List<Long> ids);
}

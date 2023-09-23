package ru.practicum.ewm.ParticipationRequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.ParticipationRequest.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface PartRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    boolean existsByRequesterAndEvent(Long requesterId, Long eventId);

    List<ParticipationRequest> findByRequester(Long requesterId);

    List<ParticipationRequest> findByEvent(Long eventId);

    List<ParticipationRequest> findByIdIn(List<Long> ids);

    @Modifying
    @Query(value = "UPDATE ParticipationRequest " +
            "SET status = :status " +
            "WHERE id IN (:ids)")
    void setStatus(List<Long> ids, String status);

    List<ParticipationRequest> findByEventAndIdIn(Long eventId, List<Long> ids);

    // для рейтинга
    Optional<ParticipationRequest> findByEventAndRequester(Long eventId, Long userId);
}

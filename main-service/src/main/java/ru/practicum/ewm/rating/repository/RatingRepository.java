package ru.practicum.ewm.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.rating.model.Rating;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByEventIdAndUserId(Long eventId, Long userId);
}

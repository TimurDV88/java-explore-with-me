package ru.practicum.ewm.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.compilation.model.CompEvent;

import java.util.List;

public interface CompEventRepository extends JpaRepository<CompEvent, Long> {

    List<CompEvent> findAllByCompId(Long compId);

    @Modifying
    @Query("DELETE CompEvent AS comp_event " +
            "WHERE comp_event.compId = :compId")
    void deleteByCompId(Long compId);
}

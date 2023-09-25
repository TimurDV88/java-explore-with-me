package ru.practicum.ewm.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import ru.practicum.ewm.compilation.model.CompEvent;

import java.util.List;

public interface CompEventRepository extends JpaRepository<CompEvent, Long> {

    List<CompEvent> findAllByCompId(Long compId);

    @Modifying
    void deleteByCompId(Long compId);
}

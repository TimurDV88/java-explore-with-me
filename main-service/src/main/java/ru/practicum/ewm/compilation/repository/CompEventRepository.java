package ru.practicum.ewm.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.compilation.model.CompEvent;

public interface CompEventRepository extends JpaRepository<CompEvent, Long> {
}

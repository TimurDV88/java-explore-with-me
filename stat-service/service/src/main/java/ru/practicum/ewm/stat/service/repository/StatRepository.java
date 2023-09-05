package ru.practicum.ewm.stat.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stat.common.model.StatRecord;

import java.time.LocalDateTime;

public interface StatRepository extends JpaRepository<StatRecord, Long> {

    @Query("SELECT COUNT(DISTINCT r.id)" + // with "DISTINCT"
            "FROM StatRecord AS r" +
            "WHERE r.timestamp > :start" +
            "AND r.timestamp < :end" +
            "AND r.uri = :uri")
    int sizeOfUniqueRecordsList(LocalDateTime start, LocalDateTime end, String uri);

    @Query("SELECT COUNT(r.id)" + // without "DISTINCT"
            "FROM StatRecord AS r" +
            "WHERE r.timestamp > :start" +
            "AND r.timestamp < :end" +
            "AND r.uri = :uri")
    int sizeOfAllRecordsList(LocalDateTime start, LocalDateTime end, String uri);
}

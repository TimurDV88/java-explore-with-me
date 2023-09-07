package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.StatRecord;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<StatRecord, Long> {

    /*
        Методы поиска записей по uri
     */
    @Query("SELECT COUNT(DISTINCT r.ip) " + // with "DISTINCT"
            "FROM StatRecord AS r " +
            "WHERE r.timestamp > :start " +
            "AND r.timestamp < :end " +
            "AND r.uri = :uri")
    int sizeOfUniqueIpRecordsListByUri(LocalDateTime start, LocalDateTime end, String uri);

    @Query("SELECT COUNT(r.id) " + // without "DISTINCT"
            "FROM StatRecord AS r " +
            "WHERE r.timestamp > :start " +
            "AND r.timestamp < :end " +
            "AND r.uri = :uri")
    int sizeOfAllRecordsListByUri(LocalDateTime start, LocalDateTime end, String uri);

    /*
        Метод поиска всех uri
    */
    @Query("SELECT r.uri " + // without "DISTINCT"
            "FROM StatRecord AS r " +
            "WHERE r.timestamp > :start " +
            "AND r.timestamp < :end")
    List<String> listOfAllUris(LocalDateTime start, LocalDateTime end);
}

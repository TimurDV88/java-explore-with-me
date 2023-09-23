package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.StatRecord;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<StatRecord, Long> {

    boolean existsByApp(String appName);

    /*
        Методы поиска записей по uri
     */
    @Query("SELECT new ru.practicum.ewm.repository.IpCountByUri(r.uri, COUNT(DISTINCT r.ip)) " + // with "DISTINCT"
            "FROM StatRecord AS r " +
            "WHERE r.app like :appName " +
            "AND r.timestamp > :start " +
            "AND r.timestamp < :end " +
            "AND r.uri IN (:uris) " +
            "GROUP BY r.uri " +
            "ORDER BY COUNT(r.ip) DESC")
    List<IpCountByUri> hitsOfUniqueIpRecordsListByUriIn(
            String appName, LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("SELECT new ru.practicum.ewm.repository.IpCountByUri(r.uri, COUNT(r.id)) " + // with "DISTINCT"
            "FROM StatRecord AS r " +
            "WHERE r.app like :appName " +
            "AND r.timestamp > :start " +
            "AND r.timestamp < :end " +
            "AND r.uri IN (:uris) " +
            "GROUP BY r.uri " +
            "ORDER BY COUNT(r.id) DESC")
    List<IpCountByUri> hitsOfAllRecordsListByUriIn(
            String appName, LocalDateTime start, LocalDateTime end, String[] uris);

    /*
        Метод поиска всех uri
    */
    @Query("SELECT r.uri " + // without "DISTINCT"
            "FROM StatRecord AS r " +
            "WHERE r.app = :appName " +
            "AND r.timestamp > :start " +
            "AND r.timestamp < :end")
    List<String> listOfAllUris(String appName, LocalDateTime start, LocalDateTime end);
}

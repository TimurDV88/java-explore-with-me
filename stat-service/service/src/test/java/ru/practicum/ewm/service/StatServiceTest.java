package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.NewStatDto;
import ru.practicum.ewm.dto.StatDtoToReturn;
import ru.practicum.ewm.model.StatRecord;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StatServiceTest {

    private final EntityManager em;

    private final StatService statService;
    private NewStatDto newStatDto;

    private String app = "ewm-main-service";
    private String uri = "/events/1";
    private String ip = "192.163.0.1";
    private String timestamp = "2000-09-06 11:00:23";

    @Test
    void shouldAdd() {

        newStatDto = new NewStatDto(app, uri, ip, timestamp);

        statService.add(newStatDto);

        TypedQuery<StatRecord> query =
                em.createQuery("Select r from StatRecord r where r.id = :id",
                        StatRecord.class);

        StatRecord record =
                query.setParameter("id", 1L)
                        .getSingleResult();

        assertEquals(app, record.getApp());
        assertEquals(uri, record.getUri());
        assertEquals(ip, record.getIp());
    }

    @Test
    void shouldGet() {

        newStatDto = new NewStatDto(app, uri, ip, timestamp);
        statService.add(newStatDto);

        String timestamp2 = "2022-11-11 00:00:23";
        String uri2 = "/events/2";
        newStatDto = new NewStatDto(app, uri2, ip, timestamp2);
        statService.add(newStatDto);

        String start = "2000-01-01 00:00:23";
        String end = "2030-11-11 00:00:23";
        String[] uris = {uri, uri2};

        List<StatDtoToReturn> list = statService.get(start, end, uris, false);

        assertEquals(list.get(0).getUri(), uri);
        assertEquals(list.get(1).getUri(), uri2);
    }
}
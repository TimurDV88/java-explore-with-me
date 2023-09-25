package ru.practicum.ewm;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.NewStatDto;
import ru.practicum.ewm.dto.StatDtoToReturn;
import ru.practicum.ewm.dto.StatMapper;
import ru.practicum.ewm.dto.StatRecordDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class StatClientService {

    private final StatClient statClient;

    public void addStatRecord(String app,
                              String uri,
                              String ip) {

        log.info("-STAT- Получен запрос на создание записи: app={}, uri={}, ip={}", app, uri, ip);

        String timestamp = LocalDateTime.now().format(StatMapper.DATE_TIME_FORMATTER);

        NewStatDto newStatDto = new NewStatDto(app, uri, ip, timestamp);

        StatRecordDto statRecordDto = statClient.post(newStatDto).getBody();

        log.info("-STAT- Запись создана: {}", statRecordDto);
    }

    public Integer getViewsByUri(String appName, String uri) {

        log.info("-STAT- Получен запрос на получение количества просмотров: app = {}, uri={}", appName, uri);

        String[] uris = new String[]{uri};
        boolean unique = true;

        String start = LocalDateTime.now().minusYears(20).format(StatMapper.DATE_TIME_FORMATTER);
        String end = LocalDateTime.now().plusYears(20).format(StatMapper.DATE_TIME_FORMATTER);

        List<StatDtoToReturn> listOfStatDto = statClient.get(appName, start, end, uris, unique).getBody();
        Integer views = 0;

        if (listOfStatDto != null) {
            views = listOfStatDto.get(0).getHits();
        }

        log.info("-STAT- Количество просмотров по uri={}: {}", uri, views);

        return views;
    }
}

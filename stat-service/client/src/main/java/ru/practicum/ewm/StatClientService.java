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
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class StatClientService {

    private final StatClient statClient;

    public StatRecordDto add(String app,
                             String uri,
                             String ip) {

        log.info("--- Получен запрос на создание записи: app={}, uri={}, ip={}",
                app, uri, ip);

        String timestamp = LocalDateTime.now().format(StatMapper.DATE_TIME_FORMATTER);

        NewStatDto newStatDto = new NewStatDto(app, uri, ip, timestamp);

        return statClient.post(newStatDto).getBody();
    }

    public List<StatDtoToReturn> get(String appName,
                                     String start,
                                     String end,
                                     String[] uris,
                                     boolean unique) throws DataFormatException {

        log.info("--- Получен запрос на получение статистики: app = {}, start={}, end={}, uris={}, unique={}",
                appName,
                start,
                end,
                Arrays.toString(uris),
                unique);

        try {

            LocalDateTime.parse(start, StatMapper.DATE_TIME_FORMATTER);
            LocalDateTime.parse(end, StatMapper.DATE_TIME_FORMATTER);

            return statClient.get(appName, start, end, uris, unique).getBody();

        } catch (Exception e) {

            log.error("- Неверный формат даты начала или конца периода: {}, {}", start, end);
            throw new DataFormatException(e.getMessage());
            //return ResponseEntity.badRequest().build();
        }


    }
}

package ru.practicum.ewm;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.NewStatDto;
import ru.practicum.ewm.dto.StatMapper;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class StatClientService {

    private final StatClient statClient;

    public ResponseEntity<Object> add(@NotNull NewStatDto newStatDto) {

        log.info("--- Получен запрос на создание записи: {}", newStatDto);

        return statClient.post(newStatDto);
    }

    public ResponseEntity<Object> get(@NotNull String start,
                                      @NotNull String end,
                                      String[] uris,
                                      boolean unique) {

        log.info("--- Получен запрос на получение статистики: start={}, end={}, uris={}, unique={}",
                start,
                end,
                Arrays.toString(uris),
                unique);

        try {

            LocalDateTime.parse(start, StatMapper.DATE_TIME_FORMATTER);
            LocalDateTime.parse(end, StatMapper.DATE_TIME_FORMATTER);

            return statClient.get(start, end, uris, unique);

        } catch (Exception e) {

            log.error("- Неверный формат даты начала или конца периода: {}, {}", start, end);
            return ResponseEntity.badRequest().build();
        }


    }
}

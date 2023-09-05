package ru.practicum.ewm.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.repository.StatRepository;
import ru.practicum.ewm.dto.FullStatDto;
import ru.practicum.ewm.dto.NewStatDto;
import ru.practicum.ewm.dto.StatDtoToReturn;
import ru.practicum.ewm.dto.StatMapper;
import ru.practicum.ewm.model.StatRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@Getter
@RequiredArgsConstructor
public class StatService {

    private final StatRepository statRepository;
    private static final String APP_NAME = "ewm-main-service";

    public FullStatDto add(NewStatDto newStatDto) {

        log.info("-- Добавление записи: {}", newStatDto);

        StatRecord record = StatMapper.newStatToRecord(newStatDto);

        FullStatDto fullDtoToReturn = StatMapper.recordToFullDto(statRepository.save(record));

        log.info("-- Запись добавлена: {}", fullDtoToReturn);

        return fullDtoToReturn;
    }

    public List<StatDtoToReturn> get(String start, String end, String[] uris, boolean unique) {

        log.info("-- Возвращение списка записей: start = {}, end = {}, uris = {}, unique = {}",
                start,
                end,
                Arrays.toString(uris),
                unique
        );

        List<StatDtoToReturn> listToReturn = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.parse(start, StatMapper.DATE_TIME_FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(end, StatMapper.DATE_TIME_FORMATTER);
        int hits;

        for (String uri : uris) {

            if (unique) {
                hits = statRepository.sizeOfUniqueRecordsList(startTime, endTime, uri);
            } else {
                hits = statRepository.sizeOfAllRecordsList(startTime, endTime, uri);
            }

            listToReturn.add(new StatDtoToReturn(APP_NAME, uri, hits));
        }

        log.info("-- Список возвращен, его размер: {}", listToReturn.size());

        return listToReturn;
    }
}

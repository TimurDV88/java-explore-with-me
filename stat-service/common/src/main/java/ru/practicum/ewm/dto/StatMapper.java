package ru.practicum.ewm.dto;

import ru.practicum.ewm.model.StatRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatMapper {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static StatRecord newStatToRecord(NewStatDto newStatDto) {

        StatRecord recordToReturn = new StatRecord();

        recordToReturn.setApp(newStatDto.getApp());
        recordToReturn.setUri(newStatDto.getUri());
        recordToReturn.setIp(newStatDto.getIp());
        recordToReturn.setTimestamp(LocalDateTime.parse(newStatDto.getTimestamp(), DATE_TIME_FORMATTER));

        return recordToReturn;
    }

    public static FullStatDto recordToFullDto(StatRecord record) {

        return new FullStatDto(
                record.getId(),
                record.getApp(),
                record.getUri(),
                record.getIp(),
                record.getTimestamp().format(DATE_TIME_FORMATTER)
        );
    }
}

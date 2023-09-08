package ru.practicum.ewm.dto;

import java.time.format.DateTimeFormatter;

public class StatMapper {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static StatRecordDto newStatToRecordDto(NewStatDto newStatDto) {

        StatRecordDto dtoToReturn = new StatRecordDto();

        dtoToReturn.setApp(newStatDto.getApp());
        dtoToReturn.setUri(newStatDto.getUri());
        dtoToReturn.setIp(newStatDto.getIp());
        dtoToReturn.setTimestamp(newStatDto.getTimestamp());

        return dtoToReturn;
    }
}

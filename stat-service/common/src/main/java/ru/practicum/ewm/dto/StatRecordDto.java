package ru.practicum.ewm.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StatRecordDto {

    private Long id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}

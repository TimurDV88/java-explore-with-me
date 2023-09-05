package ru.practicum.ewm.stat.common.dto;

import lombok.Data;

@Data
public class FullStatDto {

    private final Long id;
    private final String app;
    private final String uri;
    private final String ip;
    private final String timestamp;
}

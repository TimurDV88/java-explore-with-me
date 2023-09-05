package ru.practicum.ewm.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NewStatDto {

    private final String app;
    private final String uri;
    private final String ip;
    private final String timestamp;
}

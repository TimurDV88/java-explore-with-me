package ru.practicum.ewm.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StatDtoToReturn {

    private final String app;
    private final String uri;
    private final Integer hits;
}
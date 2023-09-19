package ru.practicum.ewm.compilation.dto;

import lombok.Data;

@Data
public class UpdateCompilationRequest {

    private final Long[] events;

    private final Boolean pinned;

    private final String title;
}

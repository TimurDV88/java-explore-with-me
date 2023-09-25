package ru.practicum.ewm.compilation.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UpdateCompilationRequest {

    private final Long[] events;

    private final Boolean pinned;

    @Size(min = 1, message = "size must be between 1 and 50")
    @Size(max = 50, message = "size must be between 1 and 50")
    private final String title;
}

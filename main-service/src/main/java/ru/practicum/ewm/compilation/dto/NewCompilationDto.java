package ru.practicum.ewm.compilation.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class NewCompilationDto {

    private final Long[] events;

    private final Boolean pinned;

    @NotNull
    @NotBlank
    @Size(min = 1)
    @Size(max = 50)
    private final String title;
}

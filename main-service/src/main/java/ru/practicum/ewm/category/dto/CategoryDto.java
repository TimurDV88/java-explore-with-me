package ru.practicum.ewm.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CategoryDto {

    private final Long id;

    @NotNull
    @NotBlank
    private final String name;
}

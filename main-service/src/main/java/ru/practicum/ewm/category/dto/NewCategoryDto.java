package ru.practicum.ewm.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class NewCategoryDto {

    private final Long id;

    @NotNull
    @NotBlank
    @Size(min = 1, message = "size must be between 1 and 50")
    @Size(max = 50, message = "size must be between 1 and 50")
    private final String name;
}

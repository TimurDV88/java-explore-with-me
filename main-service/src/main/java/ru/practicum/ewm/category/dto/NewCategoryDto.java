package ru.practicum.ewm.category.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
public class NewCategoryDto {

    @NotNull
    @NotBlank
    @Size(min = 1, message = "size must be between 1 and 50")
    @Size(max = 50, message = "size must be between 1 and 50")
    private String name;
}

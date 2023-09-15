package ru.practicum.ewm.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class NewUserDto {

    @NotNull
    @NotBlank
    private final String name;

    @NotNull
    @NotBlank
    @Email
    private final String email;
}

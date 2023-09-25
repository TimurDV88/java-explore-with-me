package ru.practicum.ewm.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class NewUserDto {

    @NotNull
    @NotBlank
    @Size(min = 2, message = "size must be between 2 and 250")
    @Size(max = 250, message = "size must be between 2 and 250")
    private final String name;

    @NotNull
    @NotBlank
    @Email
    @Size(min = 6, message = "size must be between 6 and 254")
    @Size(max = 254, message = "size must be between 6 and 254")
    private final String email;
}

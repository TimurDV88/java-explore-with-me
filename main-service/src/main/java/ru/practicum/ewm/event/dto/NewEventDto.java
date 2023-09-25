package ru.practicum.ewm.event.dto;

import lombok.Data;
import ru.practicum.ewm.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class NewEventDto {

    @NotNull
    @NotBlank
    @Size(min = 20, message = "size must be between 20 and 2000")
    @Size(max = 2000, message = "size must be between 20 and 2000")
    protected final String annotation;

    @NotNull
    protected final Long category;

    @NotNull
    @NotBlank
    @Size(min = 20, message = "size must be between 20 and 2000")
    @Size(max = 7000, message = "size must be between 20 and 7000")
    protected final String description;

    @NotNull
    protected final String eventDate;

    @NotNull
    protected final Location location;

    protected final Boolean paid;
    protected final Integer participantLimit;
    protected final Boolean requestModeration;

    @NotNull
    @NotBlank
    @Size(min = 3, message = "size must be between 3 and 120")
    @Size(max = 120, message = "size must be between 3 and 120")
    protected final String title;


    @Override
    public String toString() {

        Integer annLen;
        Integer descrLen;
        if (annotation != null) {
            annLen = annotation.length();
        } else {
            annLen = null;
        }
        if (description != null) {
            descrLen = description.length();
        } else {
            descrLen = null;
        }

        return "NewEventDto{" +
                "annotation.length='" + annLen + '\'' +
                ", category=" + category +
                ", description.length='" + descrLen + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", location=" + location +
                ", paid=" + paid +
                ", participantLimit=" + participantLimit +
                ", requestModeration=" + requestModeration +
                ", title='" + title + '\'' +
                '}';
    }
}

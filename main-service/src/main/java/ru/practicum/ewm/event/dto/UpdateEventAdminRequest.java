package ru.practicum.ewm.event.dto;

import lombok.Data;
import ru.practicum.ewm.event.model.Location;

import java.time.LocalDateTime;

@Data
public class UpdateEventAdminRequest {

    private String annotation;

    private Long category;

    private String description;

    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private StateAction stateAction;

    private String title;

    public enum StateAction {
        PUBLISH_EVENT,
        REJECT_EVENT
    }

    // - код обновления объектов (множество if-else)
    // - шаблонная функция обновления полей объектов (live coding)


}

package ru.practicum.ewm.ParticipationRequest.dto;

import lombok.Data;
import ru.practicum.ewm.ParticipationRequest.model.PartRequestState;

import java.time.LocalDateTime;

@Data
public class PartRequestDto {

    private final Long id;

    private final LocalDateTime created;

    private final Long event;

    private final Long requester;

    private final PartRequestState status;
}

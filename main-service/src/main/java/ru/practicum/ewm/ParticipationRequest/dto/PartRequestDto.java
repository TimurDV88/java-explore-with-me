package ru.practicum.ewm.ParticipationRequest.dto;

import lombok.Data;
import ru.practicum.ewm.ParticipationRequest.model.PartRequestState;

import java.time.LocalDateTime;

@Data
public class PartRequestDto {

    private final LocalDateTime created;

    private final Long eventId;

    private final Long requesterId;

    private final PartRequestState state;
}

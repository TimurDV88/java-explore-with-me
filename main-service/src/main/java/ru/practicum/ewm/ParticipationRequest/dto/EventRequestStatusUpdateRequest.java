package ru.practicum.ewm.ParticipationRequest.dto;

import lombok.Data;
import ru.practicum.ewm.ParticipationRequest.model.PartRequestUpdateState;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {

    private final List<Long> requestIds;

    private final PartRequestUpdateState partRequestState;

}

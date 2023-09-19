package ru.practicum.ewm.ParticipationRequest.dto;

import lombok.Data;
import ru.practicum.ewm.ParticipationRequest.model.PartRequestUpdateState;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    private PartRequestUpdateState partRequestState;

}

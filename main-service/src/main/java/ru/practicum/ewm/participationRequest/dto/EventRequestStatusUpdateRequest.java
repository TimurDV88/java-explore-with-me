package ru.practicum.ewm.participationRequest.dto;

import lombok.Data;
import ru.practicum.ewm.participationRequest.model.PartRequestUpdateState;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    private PartRequestUpdateState status;

}

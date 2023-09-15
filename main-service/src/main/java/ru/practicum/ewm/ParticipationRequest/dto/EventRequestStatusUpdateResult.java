package ru.practicum.ewm.ParticipationRequest.dto;

import lombok.Data;

import java.util.List;

@Data
public class EventRequestStatusUpdateResult {

    private final List<PartRequestDto> confirmedRequests;

    private final List<PartRequestDto> rejectedRequests;
}

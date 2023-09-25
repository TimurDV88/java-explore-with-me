package ru.practicum.ewm.ParticipationRequest.dto;

import ru.practicum.ewm.ParticipationRequest.model.PartRequestState;
import ru.practicum.ewm.ParticipationRequest.model.ParticipationRequest;

import java.util.ArrayList;
import java.util.List;

public class PartRequestMapper {

    public static ParticipationRequest partRequestDtoToModel(PartRequestDto partRequestDto) {

        ParticipationRequest participationRequest = new ParticipationRequest();

        participationRequest.setCreated(partRequestDto.getCreated());
        participationRequest.setEvent(partRequestDto.getEvent());
        participationRequest.setRequester(partRequestDto.getRequester());
        participationRequest.setStatus(partRequestDto.getStatus().toString());

        return participationRequest;
    }

    public static PartRequestDto partRequestToDto(ParticipationRequest participationRequest) {

        return new PartRequestDto(
                participationRequest.getId(),
                participationRequest.getCreated(),
                participationRequest.getEvent(),
                participationRequest.getRequester(),
                PartRequestState.valueOf(participationRequest.getStatus()));
    }

    public static List<PartRequestDto> partRequestToDto(List<ParticipationRequest> requests) {

        List<PartRequestDto> listToReturn = new ArrayList<>();

        for (ParticipationRequest request : requests) {
            listToReturn.add(partRequestToDto(request));
        }

        return listToReturn;
    }
}

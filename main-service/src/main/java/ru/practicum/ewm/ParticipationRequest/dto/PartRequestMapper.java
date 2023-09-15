package ru.practicum.ewm.ParticipationRequest.dto;

import ru.practicum.ewm.ParticipationRequest.model.ParticipationRequest;

import java.util.ArrayList;
import java.util.List;

public class PartRequestMapper {

    public static ParticipationRequest partRequestDtoToModel(PartRequestDto partRequestDto) {

        ParticipationRequest participationRequest = new ParticipationRequest();

        participationRequest.setCreated(partRequestDto.getCreated());
        participationRequest.setEventId(partRequestDto.getEventId());
        participationRequest.setRequesterId(partRequestDto.getRequesterId());
        participationRequest.setState(partRequestDto.getState());

        return participationRequest;
    }

    public static PartRequestDto partRequestToDto(ParticipationRequest participationRequest) {

        return new PartRequestDto(
                participationRequest.getCreated(),
                participationRequest.getEventId(),
                participationRequest.getRequesterId(),
                participationRequest.getState());
    }

    public static List<PartRequestDto> partRequestToDto(List<ParticipationRequest> requests) {

        List<PartRequestDto> listToReturn = new ArrayList<>();

        for (ParticipationRequest request : requests) {
            listToReturn.add(partRequestToDto(request));
        }

        return listToReturn;
    }
}

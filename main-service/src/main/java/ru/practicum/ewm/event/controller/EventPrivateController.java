package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ParticipationRequest.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.ParticipationRequest.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.ParticipationRequest.dto.PartRequestDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventPrivateService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {

    private final EventPrivateService eventPrivateService;


    @PostMapping
    public EventFullDto add(@PathVariable(value = "userId") Long initiatorId,
                            @RequestBody NewEventDto newEventDto) {

        return eventPrivateService.add(initiatorId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getByUserId(@PathVariable(value = "userId") Long initiatorId,
                                           @RequestParam(value = "from", defaultValue = "0") int from,
                                           @RequestParam(value = "size", defaultValue = "10") int size) {

        return eventPrivateService.getByInitiatorId(initiatorId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable(value = "userId") Long initiatorId,
                                @PathVariable Long eventId) {

        return eventPrivateService.getById(initiatorId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable(value = "userId") Long initiatorId,
                                          @PathVariable Long eventId,
                                          @RequestBody UpdateEventUserRequest updateRequest) {

        return eventPrivateService.updateEventByInitiator(initiatorId, eventId, updateRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<PartRequestDto> getRequests(@PathVariable(value = "userId") Long initiatorId,
                                            @PathVariable Long eventId) {

        return eventPrivateService.getRequests(initiatorId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(

            @PathVariable(value = "userId") Long initiatorId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest) {

        return eventPrivateService.updateRequestStatusFromInitiator(initiatorId, eventId, statusUpdateRequest);
    }
}

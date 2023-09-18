package ru.practicum.ewm.ParticipationRequest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ParticipationRequest.dto.PartRequestDto;
import ru.practicum.ewm.ParticipationRequest.service.PartRequestService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class PartRequestController {

    private final PartRequestService partRequestService;

    @PostMapping
    public PartRequestDto add(@PathVariable(value = "userId") Long requesterId,
                              @RequestParam(value = "eventId") Long eventId) {

        return partRequestService.add(requesterId, eventId);
    }

    @GetMapping
    public List<PartRequestDto> getByRequesterId(@PathVariable(value = "userId") Long requesterId) {

        return partRequestService.getByRequesterId(requesterId);
    }

    @PatchMapping("/requestId/cancel")
    public PartRequestDto cancelPartRequest(@PathVariable(value = "userId") Long requesterId,
                                            @PathVariable Long requestId) {

        return partRequestService.cancelPartRequest(requesterId, requestId);
    }

}

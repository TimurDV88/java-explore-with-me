package ru.practicum.ewm.participationRequest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.participationRequest.dto.PartRequestDto;
import ru.practicum.ewm.participationRequest.service.PartRequestService;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class PartRequestController {

    private final PartRequestService partRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartRequestDto add(@PathVariable(value = "userId") Long requesterId,
                              @RequestParam(value = "eventId") Long eventId) {

        return partRequestService.add(requesterId, eventId);
    }

    @GetMapping
    public List<PartRequestDto> getByRequesterId(@PathVariable(value = "userId") Long requesterId) {

        return partRequestService.getByRequesterId(requesterId);
    }

    @PatchMapping("/{requestId}/cancel")
    public PartRequestDto cancelPartRequest(@PathVariable(value = "userId") Long requesterId,
                                            @PathVariable(value = "requestId") Long requestId) {

        return partRequestService.cancelPartRequest(requesterId, requestId);
    }
}

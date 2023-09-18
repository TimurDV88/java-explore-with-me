package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.service.EventAdminService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventAdminService eventAdminService;

    @GetMapping
    public List<EventShortDto> getEventsByParams(

            @RequestParam(value = "categories") Long[] users,
            @RequestParam(value = "states") EventState[] states,
            @RequestParam(value = "categories") Long[] categories,
            @RequestParam(value = "rangeStart") String rangeStart,
            @RequestParam(value = "rangeEnd") String rangeEnd,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        return eventAdminService.getByParams(

                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size);
    }

    @PatchMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long eventId,
                                     @RequestBody UpdateEventAdminRequest updateRequest) {

        return eventAdminService.updateEventByAdmin(eventId, updateRequest);
    }
}

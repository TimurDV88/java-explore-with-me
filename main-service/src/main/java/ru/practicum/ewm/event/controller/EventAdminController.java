package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.service.EventAdminService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventAdminService eventAdminService;

    @GetMapping
    public List<EventShortDto> getEventsByParams(

            @RequestParam(value = "users", required = false) Long[] users,
            @RequestParam(value = "states", required = false) String[] states,
            @RequestParam(value = "categories", required = false) Long[] categories,
            @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
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
    public EventFullDto updateEventByAdmin(@PathVariable(value = "id") Long eventId,
                                           @RequestBody UpdateEventAdminRequest updateRequest) {

        return eventAdminService.updateEventByAdmin(eventId, updateRequest);
    }
}

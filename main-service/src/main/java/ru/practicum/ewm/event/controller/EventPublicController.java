package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.StatClientService;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventPublicService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventPublicController {



    private final EventPublicService eventPublicService;



    @GetMapping
    public List<EventShortDto> getByParameters(

            HttpServletRequest request,

            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "categories", required = false) Long[] categories,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(value = "sort", defaultValue = "EVENT_DATE") String sort,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size

    ) {

        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        return eventPublicService.getByParameters(uri, ip,

                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size);
    }

    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable (value = "id") Long eventId, HttpServletRequest request) {

        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        return eventPublicService.getById(eventId, uri, ip);
    }

}

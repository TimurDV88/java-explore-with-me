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

    @Value("${app-name}")
    private String appName;

    private final EventPublicService eventPublicService;

    private final StatClientService statClientService;

    @GetMapping
    public List<EventShortDto> getEventsByParams(

            HttpServletRequest request,

            @RequestParam(value = "text", defaultValue = "") String text,
            @RequestParam(value = "categories") Integer[] categories,
            @RequestParam(value = "paid") Boolean paid,
            @RequestParam(value = "rangeStart") String rangeStart,
            @RequestParam(value = "rangeEnd") String rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size

    ) {

        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        statClientService.add(appName, uri, ip);

        return eventPublicService.getByParameters(

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
    public EventFullDto getEventById(@PathVariable Long eventId, HttpServletRequest request) {

        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        statClientService.add(appName, uri, ip);

        return eventPublicService.getById(eventId);
    }

}

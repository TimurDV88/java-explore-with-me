package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.StatEndPoints;
import ru.practicum.ewm.dto.NewStatDto;
import ru.practicum.ewm.dto.StatDtoToReturn;
import ru.practicum.ewm.dto.StatRecordDto;
import ru.practicum.ewm.service.StatService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatServiceController {

    private final StatService statService;

    @PostMapping(StatEndPoints.POST_RECORD_PATH)
    public StatRecordDto addNewStatRecord(@RequestBody NewStatDto newStatDto) {

        return statService.add(newStatDto);
    }

    @GetMapping(StatEndPoints.GET_STAT_PATH)
    public List<StatDtoToReturn> getRecordsByParams(

            @RequestParam(value = "app") String appName,
            @RequestParam(value = "start", defaultValue = "none") String start,
            @RequestParam(value = "end", defaultValue = "none") String end,
            @RequestParam(value = "uris", required = false) String[] uris,
            @RequestParam(value = "unique", defaultValue = "false") boolean unique
    ) {

        return statService.get(appName, start, end, uris, unique);
    }
}

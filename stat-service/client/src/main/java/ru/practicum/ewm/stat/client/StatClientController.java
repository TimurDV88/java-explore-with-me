package ru.practicum.ewm.stat.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.stat.common.StatEndPoints;
import ru.practicum.ewm.stat.common.dto.NewStatDto;


@Controller
@RequiredArgsConstructor
public class StatClientController {

    private final StatClientService statClientService;

    @PostMapping(StatEndPoints.POST_RECORD_PATH)
    public ResponseEntity<Object> addNewStatRecord(@RequestBody NewStatDto newStatDto) {

        return statClientService.add(newStatDto);
    }

    @GetMapping(StatEndPoints.GET_STAT_PATH)
    public ResponseEntity<Object> getRecordsByParams(

            @RequestParam(value = "start") String start,
            @RequestParam(value = "end") String end,
            @RequestParam(value = "uris") String[] uris,
            @RequestParam(value = "unique", defaultValue = "false") boolean unique
    ) {

        return statClientService.get(start, end, uris, unique);
    }
}

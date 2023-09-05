package ru.practicum.ewm.stat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.stat.common.StatEndPoints;
import ru.practicum.ewm.stat.common.dto.FullStatDto;
import ru.practicum.ewm.stat.common.dto.NewStatDto;
import ru.practicum.ewm.stat.common.dto.StatDtoToReturn;
import ru.practicum.ewm.stat.service.service.StatService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StatServiceController {

    private final StatService statService;

    @PostMapping(StatEndPoints.POST_RECORD_PATH)
    public FullStatDto addNewStatRecord(@RequestBody NewStatDto newStatDto) {

        return statService.add(newStatDto);
    }

    @GetMapping(StatEndPoints.GET_STAT_PATH)
    public List<StatDtoToReturn> getRecordsByParams(

            @RequestParam(value = "start") String start,
            @RequestParam(value = "end") String end,
            @RequestParam(value = "uris") String[] uris,
            @RequestParam(value = "unique", defaultValue = "false") boolean unique
    ) {

        return statService.get(start, end, uris, unique);
    }
}

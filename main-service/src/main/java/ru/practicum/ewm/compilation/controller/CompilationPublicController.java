package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationPublicService;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationPublicController {

    private final CompilationPublicService compilationPublicService;

    @GetMapping
    public List<CompilationDto> getByParameters(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                                @RequestParam(value = "from", defaultValue = "0") int from,
                                                @RequestParam(value = "size", defaultValue = "10") int size) {

        return compilationPublicService.getByParameters(pinned, from, size);
    }

    @GetMapping("/{id}")
    public CompilationDto getById(@PathVariable(value = "id") Long compId) {

        return compilationPublicService.getById(compId);
    }
}

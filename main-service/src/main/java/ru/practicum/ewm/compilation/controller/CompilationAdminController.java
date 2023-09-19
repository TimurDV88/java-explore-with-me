package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.CompilationAdminService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {

    private final CompilationAdminService compilationAdminService;

    @PostMapping
    public CompilationDto add(@RequestBody NewCompilationDto newCompilationDto) {

        return compilationAdminService.add(newCompilationDto);
    }

    @PatchMapping("/{id}")
    public CompilationDto update(@PathVariable(value = "id") Long compId,
                                 @RequestBody UpdateCompilationRequest updateRequest) {

        return compilationAdminService.update(compId, updateRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(value = "id") Long compId) {

        compilationAdminService.delete(compId);
    }
}

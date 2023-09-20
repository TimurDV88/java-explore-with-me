package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.CompilationAdminService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {

    private final CompilationAdminService compilationAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto add(@RequestBody @Valid NewCompilationDto newCompilationDto) {

        return compilationAdminService.add(newCompilationDto);
    }

    @PatchMapping("/{id}")
    public CompilationDto update(@PathVariable(value = "id") Long compId,
                                 @RequestBody @Valid UpdateCompilationRequest updateRequest) {

        return compilationAdminService.update(compId, updateRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(value = "id") Long compId) {

        compilationAdminService.delete(compId);
    }
}

package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryAdminService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {

    private final CategoryAdminService categoryAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@RequestBody @Valid NewCategoryDto newCategoryDto) {

        return categoryAdminService.add(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeById(@PathVariable @NotNull Long catId) {

        categoryAdminService.removeById(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateById(@PathVariable @NotNull Long catId,
                                  @RequestBody @Valid NewCategoryDto newCategoryDto) {

        return categoryAdminService.updateById(catId, newCategoryDto);
    }
}

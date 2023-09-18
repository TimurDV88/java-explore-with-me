package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryAdminService;

import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {

    private final CategoryAdminService categoryAdminService;

    @PostMapping
    public CategoryDto add(@RequestBody @NotNull CategoryDto categoryDto) {

        return categoryAdminService.add(categoryDto);
    }

    @DeleteMapping("/{catId}")
    public void removeById(@PathVariable @NotNull Long catId) {

        categoryAdminService.removeById(catId);
    }

    @PatchMapping("/{catId}")
    public void updateById(@PathVariable @NotNull Long catId,
                           @RequestBody CategoryDto categoryDto) {

        categoryAdminService.updateById(catId, categoryDto);
    }
}

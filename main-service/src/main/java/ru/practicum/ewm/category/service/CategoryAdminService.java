package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.error.exception.ConflictOnRequestException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.event.repository.EventRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryAdminService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public CategoryDto add(NewCategoryDto newCategoryDto) {

        log.info("-- Сохранение категории: {}", newCategoryDto);

        // блок проверок
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ConflictOnRequestException("- Такое имя категории уже есть в базе, категория не сохранена");
        }
        // конец блока проверок

        Category category = CategoryMapper.newCategoryDtoToModel(newCategoryDto);

        category = categoryRepository.save(category);

        CategoryDto categoryDtoToReturn = CategoryMapper.categoryToDto(category);

        log.info("-- Категория сохранена: {}", categoryDtoToReturn);

        return categoryDtoToReturn;
    }

    public CategoryDto updateById(Long categoryId, NewCategoryDto newCategoryDto) {

        log.info("-- Обновление категории №{}", categoryId);

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("- Категория №" + categoryId + " не найдена в базе"));

        // блок проверок
        if (categoryRepository.existsByName(newCategoryDto.getName())
                && !category.getName().equals(newCategoryDto.getName())) {
            throw new ConflictOnRequestException("- Такое имя категории уже есть в базе, категория не обновлена");
        }
        // конец блока проверок

        category.setName(newCategoryDto.getName());

        CategoryDto categoryDtoToReturn = CategoryMapper.categoryToDto(categoryRepository.save(category));

        log.info("-- Категория обновлена: {}", newCategoryDto);

        return categoryDtoToReturn;
    }

    public void removeById(Long categoryId) {

        log.info("-- Удаление категории №{}", categoryId);

        Category categoryToCheck = categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("- Категория №" + categoryId + " не найдена в базе"));

        // блок проверок
        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new ConflictOnRequestException("- Нельзя удалить категорию с привязанными событиями");
        }
        // конец блока проверок

        CategoryDto categoryToShowInLog = CategoryMapper.categoryToDto(categoryToCheck);

        categoryRepository.deleteById(categoryId);

        log.info("-- Категория удалена: {}", categoryToShowInLog);
    }
}

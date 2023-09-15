package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.error.exception.NotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryDto add(CategoryDto categoryDto) {

        log.info("-- Сохранение категории: {}", categoryDto);

        Category category = CategoryMapper.categoryDtoToModel(categoryDto);

        category = categoryRepository.save(category);

        CategoryDto categoryDtoToReturn = CategoryMapper.categoryToDto(category);

        log.info("-- Категория сохранена: {}", categoryDtoToReturn);

        return categoryDtoToReturn;
    }

    public CategoryDto updateById(Long categoryId, CategoryDto categoryDto) {

        log.info("-- Обновление категории №{}", categoryId);

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("- Категория №" + categoryId + " не найдена в базе"));

        category.setName(categoryDto.getName());

        CategoryDto categoryDtoToReturn = CategoryMapper.categoryToDto(categoryRepository.save(category));

        log.info("-- Категория обновлена: {}", categoryDto);

        return categoryDtoToReturn;
    }

    public void delete(Long categoryId) {

        log.info("-- Удаление категории №{}", categoryId);

        Category categoryToCheck = categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("- Категория №" + categoryId + " не найдена в базе"));

        CategoryDto categoryToShowInLog = CategoryMapper.categoryToDto(categoryToCheck);

        categoryRepository.deleteById(categoryId);

        log.info("-- Категория удалена: {}", categoryToShowInLog);
    }
}

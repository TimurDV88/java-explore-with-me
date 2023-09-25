package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.error.exception.IncorrectRequestException;
import ru.practicum.ewm.error.exception.NotFoundException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryPublicService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getAll(int from, int size) {

        log.info("-- Возвращение всех категорий");

        PageRequest pageRequest;

        if (size > 0 && from >= 0) {
            int page = from / size;
            pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        } else {
            throw new IncorrectRequestException("- Размер страницы должен быть > 0, 'from' должен быть >= 0");
        }

        List<CategoryDto> listToReturn = CategoryMapper.categoryToDto(categoryRepository.findAll(pageRequest));

        log.info("-- Список категорий возвращен, его размер: {}", listToReturn.size());

        return listToReturn;
    }

    public CategoryDto getById(Long categoryId) {

        log.info("-- Возвращение категории с id={}", categoryId);

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("- Категория с id=" + categoryId + " не найдена"));

        CategoryDto categoryDto = CategoryMapper.categoryToDto(category);

        log.info("-- Категория с id={} возвращёна", categoryId);

        return categoryDto;
    }
}

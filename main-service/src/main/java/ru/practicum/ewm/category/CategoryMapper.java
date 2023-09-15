package ru.practicum.ewm.category;

public class CategoryMapper {

    public static CategoryDto categoryToDto(Category category) {

        return new CategoryDto(category.getId(), category.getName());
    }

    public static Category categoryDtoToModel(CategoryDto categoryDto) {

        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());

        return category;
    }
}

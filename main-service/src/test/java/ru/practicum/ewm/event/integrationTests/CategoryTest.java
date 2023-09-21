package ru.practicum.ewm.event.integrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.controller.CategoryAdminController;
import ru.practicum.ewm.category.controller.CategoryPublicController;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CategoryTest {

    private final CategoryPublicController categoryPublicController;
    private final CategoryAdminController categoryAdminController;


    @BeforeEach
    void setUp() {


    }

    @Test
    void add() {

        String name1 = "category1";
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName(name1);

        CategoryDto categoryDto = categoryAdminController.add(newCategoryDto);

        assertEquals(name1, categoryDto.getName());

        try {
            categoryAdminController.add(newCategoryDto);
        } catch (Exception e) {
            System.out.println("\nexception class=" + e.getClass());
        }

    }

    @Test
    void updateById() {
    }
}
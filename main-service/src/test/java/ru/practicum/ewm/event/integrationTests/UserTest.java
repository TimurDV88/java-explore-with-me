package ru.practicum.ewm.event.integrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.user.controller.UserAdminController;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.UserFullDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserTest {
    private final UserAdminController userAdminController;


    @BeforeEach
    void setUp() {


    }

    @Test
    void add() {

        String name = "name1";
        String email = "email1@c.c";
        NewUserDto newUserDto = new NewUserDto(name, email);

        UserFullDto userFullDto = userAdminController.add(newUserDto);

        assertEquals(email, userFullDto.getEmail());

        userAdminController.add(newUserDto);

        try {
            userAdminController.add(newUserDto);
        } catch (Exception e) {
            System.out.println("\nexception class=" + e.getClass());
        }

    }

    @Test
    void updateById() {
    }
}
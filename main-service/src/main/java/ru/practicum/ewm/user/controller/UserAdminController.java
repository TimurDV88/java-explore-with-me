package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.UserFullDto;
import ru.practicum.ewm.user.service.UserAdminService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserAdminController {

    private final UserAdminService userAdminService;

    @PostMapping
    public UserFullDto add(@RequestBody @Valid NewUserDto newUserDto) {

        return userAdminService.add(newUserDto);
    }

    @GetMapping
    public List<UserFullDto> get(@RequestParam(value = "ids") @NotNull Long[] userIds,
                                 @RequestParam(value = "from", defaultValue = "0") int from,
                                 @RequestParam(value = "size", defaultValue = "10") int size) {

        return userAdminService.get(userIds, from, size);
    }

    @DeleteMapping("/{userId}")
    public void removeById(@PathVariable @NotNull Long userId) {

        userAdminService.removeById(userId);
    }
}

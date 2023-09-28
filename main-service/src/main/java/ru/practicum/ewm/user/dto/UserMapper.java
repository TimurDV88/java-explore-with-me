package ru.practicum.ewm.user.dto;

import ru.practicum.ewm.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static User newUserDtoToUser(NewUserDto newUserDto) {

        User user = new User();

        user.setName(newUserDto.getName());
        user.setEmail(newUserDto.getEmail());

        return user;
    }

    public static UserFullDto userToFullDto(User user) {

        return new UserFullDto(user.getId(), user.getName(), user.getEmail(), user.getRating());
    }

    public static List<UserFullDto> userToFullDto(Iterable<User> users) {

        List<UserFullDto> toReturn = new ArrayList<>();

        for (User user : users) {
            toReturn.add(userToFullDto(user));
        }

        //toReturn.sort(Comparator.reverseOrder());

        return toReturn;
    }

    public static UserShortDto userToShortDto(User user) {

        return new UserShortDto(user.getId(), user.getName(), user.getRating());
    }

    public static List<UserShortDto> userToShortDto(Iterable<User> users) {

        List<UserShortDto> userShortDtoList = new ArrayList<>();

        for (User user : users) {
            userShortDtoList.add(userToShortDto(user));
        }
        return userShortDtoList;
    }
}

package ru.practicum.ewm.user.dto;

import lombok.Data;

@Data
public class UserFullDto implements Comparable<UserFullDto> {

    private final Long id;

    private final String name;

    private final String email;

    @Override
    public int compareTo(UserFullDto o) {
        return Long.compare(this.id, o.id);
    }
}

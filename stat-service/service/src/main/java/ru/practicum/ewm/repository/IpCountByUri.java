package ru.practicum.ewm.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IpCountByUri {

    private String uri;
    private Long hits;
}

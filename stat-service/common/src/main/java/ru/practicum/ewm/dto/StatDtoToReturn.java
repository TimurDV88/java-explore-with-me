package ru.practicum.ewm.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StatDtoToReturn implements Comparable<StatDtoToReturn> {

    private final String app;
    private final String uri;
    private final Integer hits;

    @Override
    public int compareTo(StatDtoToReturn o) {
        return Integer.compare(this.hits, o.hits);
    }
}

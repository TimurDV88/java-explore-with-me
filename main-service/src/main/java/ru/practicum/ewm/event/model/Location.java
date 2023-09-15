package ru.practicum.ewm.event.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Location {

    private double lat;
    private double lon;
}

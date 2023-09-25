package ru.practicum.ewm.event.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@RequiredArgsConstructor
public class Location {

    private double lat;

    private double lon;
}

package ru.practicum.ewm.ParticipationRequest.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "part_requests")
@Getter
@Setter
@ToString
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "event", nullable = false)
    private Long event;

    @Column(name = "requester", nullable = false)
    private Long requester;

    @Column(name = "status")
    private String status;
}

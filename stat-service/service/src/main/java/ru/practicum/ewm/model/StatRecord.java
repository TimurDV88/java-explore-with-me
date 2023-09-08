package ru.practicum.ewm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.dto.StatMapper;
import ru.practicum.ewm.dto.StatRecordDto;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "records")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class StatRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app", nullable = false)
    private String app;

    @Column(name = "uri", nullable = false)
    private String uri;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public StatRecord(StatRecordDto statRecordDto) {

        this.id = statRecordDto.getId();
        this.app = statRecordDto.getApp();
        this.uri = statRecordDto.getUri();
        this.ip = statRecordDto.getIp();
        this.timestamp = LocalDateTime.parse(statRecordDto.getTimestamp(), StatMapper.DATE_TIME_FORMATTER);
    }
}

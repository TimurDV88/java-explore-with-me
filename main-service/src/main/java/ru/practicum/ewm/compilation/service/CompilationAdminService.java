package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompMapper;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.CompEvent;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompEventRepository;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationAdminService {

    private final CompilationRepository compilationRepository;
    private final CompEventRepository compEventRepository;
    private final EventRepository eventRepository;

    public CompilationDto add(NewCompilationDto newCompilationDto) {

        log.info("-- Добавление подборки событий: {}", newCompilationDto);

        Compilation compilation = CompMapper.newCompDtoToCompilation(newCompilationDto);
        Long compId = compilationRepository.save(compilation).getId();
        Long[] eventIds = newCompilationDto.getEvents();

        List<CompEvent> compEventList = new ArrayList<>();

        // сохраняем compEvent-ы в список
        for (Long eventId : eventIds) {

            CompEvent compEvent = new CompEvent();
            compEvent.setCompId(compId);
            compEvent.setEventId(eventId);
            compEventList.add(compEvent);
        }
        // сохраняем список compEvent-ов в репозиторий
        compEventRepository.saveAll(compEventList);

        // получаем список EventShortDto
        List<EventShortDto> eventShortDtos = EventMapper.eventToShortDto(eventRepository.findAllByIdIn(eventIds));

        CompilationDto result = CompMapper.compilationToDto(compilation, eventShortDtos);

        log.info("-- Подборка событий добавлена: id={}, размер={}", compId, eventShortDtos.size());

        return result;
    }
}

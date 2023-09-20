package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompMapper;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.model.CompEvent;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompEventRepository;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.error.exception.NotFoundException;
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

        // создаём Compilation
        Compilation compilation = new Compilation();

        if (newCompilationDto.getPinned() != null) {
            compilation.setPinned(newCompilationDto.getPinned());
        } else {
            compilation.setPinned(false);
        }

        compilation.setTitle(newCompilationDto.getTitle());

        compilation = compilationRepository.save(compilation);

        // создаём compEventList
        Long compId = compilation.getId();

        Long[] eventIds = new Long[]{};
        if (newCompilationDto.getEvents() != null) {
            eventIds = newCompilationDto.getEvents();
        }

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
        List<EventShortDto> eventShortDtoList = EventMapper.eventToShortDto(eventRepository.findAllByIdIn(eventIds));

        CompilationDto result = CompMapper.compilationToDto(compilation, eventShortDtoList);

        log.info("-- Подборка событий добавлена: id={}, title={}, размер={}",
                compId, result.getTitle(), eventShortDtoList.size());

        return result;
    }

    public CompilationDto update(Long compId, UpdateCompilationRequest updateRequest) {

        log.info("-- Обновление подборки событий: {}", updateRequest);

        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("- Подборка с id=" + compId + " не найдена"));

        // обновляем pinned
        if (updateRequest.getPinned() != null) {
            compilation.setPinned(updateRequest.getPinned());
        }

        // обновляем title
        if (updateRequest.getTitle() != null) {
            compilation.setTitle(updateRequest.getTitle());
        }

        Long[] eventIds = compEventRepository.findAllByCompId(compId).stream()
                .map(CompEvent::getEventId)
                .toArray(Long[]::new);

        // обновляем events
        if (updateRequest.getEvents() != null) {

            eventIds = updateRequest.getEvents();

            List<CompEvent> compEventList = new ArrayList<>();

            //удаляем старые compEvent-ы
            compEventRepository.deleteByCompId(compId);

            // сохраняем новые compEvent-ы в список
            for (Long eventId : eventIds) {

                CompEvent compEvent = new CompEvent();
                compEvent.setCompId(compId);
                compEvent.setEventId(eventId);
                compEventList.add(compEvent);
            }

            // сохраняем список новых compEvent-ов в репозиторий
            compEventRepository.saveAll(compEventList);
        }

        // получаем список EventShortDto
        List<EventShortDto> eventShortDtos = EventMapper.eventToShortDto(eventRepository.findAllByIdIn(eventIds));

        CompilationDto result = CompMapper.compilationToDto(compilation, eventShortDtos);

        log.info("-- Подборка событий обновлена: id={}, title={}, размер={}",
                compId, result.getTitle(), eventShortDtos.size());

        return result;
    }

    public void delete(Long compId) {

        log.info("-- Удаление подборки событий №{}", compId);

        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("- Подборка с id=" + compId + " не найдена");
        }

        compilationRepository.deleteById(compId);
        //compEventRepository.deleteByCompId(compId); - не требуется, т.к. в схеме указано CASCADE

        log.info("-- Подборка событий №{} удалена", compId);
    }
}

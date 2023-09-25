package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompMapper;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.model.CompEvent;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompEventRepository;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.error.exception.IncorrectRequestException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationPublicService {

    private final CompilationRepository compilationRepository;
    private final CompEventRepository compEventRepository;
    private final EventRepository eventRepository;

    /*
        Вспомогательный метод генерирования CompilationDto
     */
    private CompilationDto getCompilationDto(Long compId) {

        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("- Подборка с id=" + compId + " не найдена"));

        Long[] eventIds = compEventRepository.findAllByCompId(compId).stream()
                .map(CompEvent::getEventId)
                .toArray(Long[]::new);

        // получаем список EventShortDto
        List<EventShortDto> eventShortDtos = EventMapper.eventToShortDto(eventRepository.findAllByIdIn(eventIds));

        return CompMapper.compilationToDto(compilation, eventShortDtos);
    }

    public List<CompilationDto> getByParameters(Boolean pinned, int from, int size) {

        log.info("-- Возвращение подборок с параметрами (Public): pinned={}, from={}, size={}",
                pinned, from, size);

        // блок пагинации
        PageRequest pageRequest;

        if (size > 0 && from >= 0) {
            int page = from / size;
            pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        } else {
            throw new IncorrectRequestException("- Размер страницы должен быть > 0, 'from' должен быть >= 0");
        }

        Iterable<Compilation> compilationList;
        if (pinned != null) {
            compilationList = compilationRepository.findByPinned(pinned, pageRequest);
        } else {
            compilationList = compilationRepository.findAll(pageRequest);
        }

        List<CompilationDto> resultList = new ArrayList<>();

        for (Compilation compilation : compilationList) {

            resultList.add(getCompilationDto(compilation.getId()));
        }

        log.info("-- Список подборок (Public) возвращен, его размер: {}", resultList.size());

        return resultList;
    }

    public CompilationDto getById(Long compId) {

        log.info("-- Возвращение (Public) подборки №{}", compId);

        CompilationDto compilationDto = getCompilationDto(compId);

        log.info("-- Подборка №{} возвращена (Public)", compId);

        return compilationDto;
    }
}

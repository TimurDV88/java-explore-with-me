package ru.practicum.ewm.compilation.dto;

import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

public class CompMapper {

    public static Compilation newCompDtoToCompilation(NewCompilationDto newCompilationDto) {

        Compilation compilation = new Compilation();
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());

        return compilation;
    }

    public static CompilationDto compilationToDto(Compilation compilation, List<EventShortDto> events) {

        return new CompilationDto(compilation.getId(), events, compilation.getPinned(), compilation.getTitle());
    }
}

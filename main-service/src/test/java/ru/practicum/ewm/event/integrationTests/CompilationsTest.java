package ru.practicum.ewm.event.integrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.controller.CategoryAdminController;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.compilation.controller.CompilationAdminController;
import ru.practicum.ewm.compilation.controller.CompilationPublicController;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.repository.CompEventRepository;
import ru.practicum.ewm.event.controller.EventPrivateController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.controller.UserAdminController;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.UserFullDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CompilationsTest {

    private final CompilationAdminController compilationAdminController;
    private final CompilationPublicController compilationPublicController;
    private final CompEventRepository compEventRepository;

    private final EventPrivateController eventPrivateController;
    private final CategoryAdminController categoryAdminController;
    private final UserAdminController userAdminController;

    private static EventFullDto eventFullDto1;
    private static EventFullDto eventFullDto2;
    private static EventFullDto eventFullDto3;
    private static EventFullDto eventFullDto4;
    private static NewCompilationDto newCompilationDto;
    private static CompilationDto compilationDto;

    @BeforeEach
    void setUp() {

        UserFullDto userFullDto = userAdminController.add(new NewUserDto("user_name", "email@email.com"));

        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("category_name");
        Long category = categoryAdminController.add(newCategoryDto).getId();

        String eventDate = LocalDateTime.now().plusDays(1).format(EventMapper.DATE_TIME_FORMATTER);

        Location location = new Location();
        location.setLat(100);
        location.setLon(100);

        Boolean paid = true;
        Integer participantLimit = 100;
        Boolean requestModeration = true;

        // event1
        NewEventDto newEventDto = new NewEventDto("annotationEvent1", category, "descriptionEvent1",
                eventDate, location, paid, participantLimit, requestModeration, "titleEvent1");
        eventFullDto1 = eventPrivateController.add(userFullDto.getId(), newEventDto);

        // event2
        newEventDto = new NewEventDto("annotationEvent2", category, "descriptionEvent2",
                eventDate, location, paid, participantLimit, requestModeration, "titleEvent2");
        eventFullDto2 = eventPrivateController.add(userFullDto.getId(), newEventDto);

        // event3
        newEventDto = new NewEventDto("annotationEvent3", category, "descriptionEvent3",
                eventDate, location, paid, participantLimit, requestModeration, "titleEvent3");
        eventFullDto3 = eventPrivateController.add(userFullDto.getId(), newEventDto);

        // event4
        newEventDto = new NewEventDto("annotationEvent4", category, "descriptionEvent4",
                eventDate, location, paid, participantLimit, requestModeration, "titleEvent4");
        eventFullDto4 = eventPrivateController.add(userFullDto.getId(), newEventDto);
    }


    @Test
    void addEmptyCollection() {

        newCompilationDto = new NewCompilationDto(null, null, "emptyCompilation");
        compilationDto = compilationAdminController.add(newCompilationDto);
        assertEquals(newCompilationDto.getTitle(), compilationDto.getTitle());
        assertEquals(0, compilationDto.getEvents().size());
    }

    @Test
    void addAndGetAndUpdateCollection() {

        // add comp1 with event1
        newCompilationDto = new NewCompilationDto(new Long[]{eventFullDto1.getId()}, false, "Compilation1");
        compilationDto = compilationAdminController.add(newCompilationDto);
        Long comp1id = compilationDto.getId();
        assertEquals(newCompilationDto.getTitle(), compilationDto.getTitle());
        assertEquals(1, compilationDto.getEvents().size());
        assertEquals(eventFullDto1.getTitle(), compilationDto.getEvents().get(0).getTitle());

        // add comp2 with event2 and event 3
        newCompilationDto = new NewCompilationDto(new Long[]{eventFullDto2.getId(), eventFullDto3.getId()},
                false, "Compilation2");
        compilationDto = compilationAdminController.add(newCompilationDto);
        Long comp2id = compilationDto.getId();
        assertEquals(newCompilationDto.getTitle(), compilationDto.getTitle());
        assertEquals(2, compilationDto.getEvents().size());

        // get comp1 (public)
        compilationDto = compilationPublicController.getById(comp1id);
        assertEquals(1, compilationDto.getEvents().size());
        assertEquals(eventFullDto1.getTitle(), compilationDto.getEvents().get(0).getTitle());

        //update title for comp1 (admin)
        UpdateCompilationRequest updateRequest =
                new UpdateCompilationRequest(null, null, "UpdatedCompilation1");
        compilationAdminController.update(comp1id, updateRequest);
        compilationDto = compilationPublicController.getById(comp1id);
        assertEquals(updateRequest.getTitle(), compilationDto.getTitle());

        //update pinned for comp1 (admin)
        updateRequest =
                new UpdateCompilationRequest(null, true, null);
        compilationAdminController.update(comp1id, updateRequest);
        compilationDto = compilationPublicController.getById(comp1id);
        assertEquals(updateRequest.getPinned(), compilationDto.getPinned());

        //update events for comp1 (admin)
        System.out.println("\ncompEventRep before updating: " + compEventRepository.findAll() + "\n");

        updateRequest =
                new UpdateCompilationRequest(new Long[]{eventFullDto1.getId(), eventFullDto4.getId()},
                        null, null);
        compilationAdminController.update(comp1id, updateRequest);
        compilationDto = compilationPublicController.getById(comp1id);

        System.out.println("\ncompEventRep after updating: " + compEventRepository.findAll() + "\n");

        assertEquals(2, compilationDto.getEvents().size());
        assertEquals(eventFullDto4.getTitle(), compilationDto.getEvents().get(1).getTitle());

        //get by null params (public)
        List<CompilationDto> compList =
                compilationPublicController.getByParameters(null, 0, 10);

        System.out.println("\ncompList=" + compList.toString() + "\n");

        assertEquals(2, compList.size());

        //get by pinned=true (public)
        compList =
                compilationPublicController.getByParameters(true, 0, 10);
        assertEquals(1, compList.size());
        assertEquals(comp1id, compList.get(0).getId());

        //get by pinned=false (public)
        compList =
                compilationPublicController.getByParameters(false, 0, 10);
        assertEquals(1, compList.size());
        assertEquals(comp2id, compList.get(0).getId());

        //delete by id (admin)
        System.out.println("\ncompEventRep before deleting: " + compEventRepository.findAll() + "\n");

        compilationAdminController.delete(comp2id);

        compList =
                compilationPublicController.getByParameters(null, 0, 10);
        assertEquals(1, compList.size());
        assertEquals(comp1id, compList.get(0).getId());

        System.out.println("\ncompEventRep after deleting: " + compEventRepository.findAll() + "\n");
    }
}

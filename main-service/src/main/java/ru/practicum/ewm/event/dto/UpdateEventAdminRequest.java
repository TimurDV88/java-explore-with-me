package ru.practicum.ewm.event.dto;

import lombok.Data;
import ru.practicum.ewm.event.model.Location;

import javax.validation.constraints.Size;

@Data
public class UpdateEventAdminRequest {

    @Size(min = 20, message = "size must be between 20 and 2000")
    @Size(max = 2000, message = "size must be between 20 and 2000")
    private String annotation;

    private Long category;

    @Size(min = 20, message = "size must be between 20 and 7000")
    @Size(max = 7000, message = "size must be between 20 and 7000")
    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private StateAction stateAction;

    @Size(min = 3, message = "size must be between 3 and 120")
    @Size(max = 120, message = "size must be between 3 and 120")
    private String title;

    @Override
    public String toString() {

        Integer annLen;
        Integer descrLen;
        if (annotation != null) {
            annLen = annotation.length();
        } else {
            annLen = null;
        }
        if (description != null) {
            descrLen = description.length();
        } else {
            descrLen = null;
        }

        return "UpdateEventAdminRequest{" +
                "annotation.length='" + annLen + '\'' +
                ", category=" + category +
                ", description.length='" + descrLen + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", location=" + location +
                ", paid=" + paid +
                ", participantLimit=" + participantLimit +
                ", requestModeration=" + requestModeration +
                ", stateAction=" + stateAction +
                ", title='" + title + '\'' +
                '}';
    }

    public enum StateAction {
        PUBLISH_EVENT,
        REJECT_EVENT
    }

}

package account.utils;

import account.model.entity.InfoSecEvent;
import account.model.enums.LoggingEvents;

import java.time.LocalDateTime;

public class CreateInfoSecEvent {

    public static InfoSecEvent createEvent(String path,
                                           String email,
                                           LoggingEvents action,
                                           String object) {
        InfoSecEvent event = new InfoSecEvent();
        event.setDate(LocalDateTime.now());
        event.setAction(action);
        event.setSubject(email);
        event.setObject(object);
        event.setPath(path);

        return event;
    }
}

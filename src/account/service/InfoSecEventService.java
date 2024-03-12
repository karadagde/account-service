package account.service;

import account.model.entity.InfoSecEvent;
import account.repository.InfoSecEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InfoSecEventService {

    private final InfoSecEventRepository infoSecEventRepository;

    @Autowired
    public InfoSecEventService(InfoSecEventRepository infoSecEventRepository) {
        this.infoSecEventRepository = infoSecEventRepository;
    }


    public void logEvent(InfoSecEvent event) {
        infoSecEventRepository.save(event);
    }

    public List<InfoSecEvent> getAllEvents() {
        List<InfoSecEvent> events = new ArrayList<>();
        infoSecEventRepository.findAll().forEach(events::add);
        return events;
    }

}

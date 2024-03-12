package account.controller;

import account.model.entity.InfoSecEvent;
import account.service.InfoSecEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/security")
public class AuditController {

    private final InfoSecEventService infoSecEventService;

    @Autowired
    public AuditController(InfoSecEventService infoSecEventService) {
        this.infoSecEventService = infoSecEventService;
    }


    @GetMapping("/events/")
    public ResponseEntity<List<InfoSecEvent>> getAllEvents() {
        return ResponseEntity.ok(infoSecEventService.getAllEvents());
    }
}

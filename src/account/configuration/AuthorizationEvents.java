package account.configuration;

import account.model.dto.data.AppUserDTO;
import account.model.entity.AppUser;
import account.model.entity.InfoSecEvent;
import account.model.enums.LoggingEvents;
import account.service.InfoSecEventService;
import account.utils.CreateInfoSecEvent;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthorizationEvents {

    private final InfoSecEventService infoSecEventService;

    public AuthorizationEvents(InfoSecEventService infoSecEventService) {
        this.infoSecEventService = infoSecEventService;
    }

    @EventListener
    public void onFailure(AuthorizationDeniedEvent failures) {
        System.out.println("Authorization denied entrance");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        System.out.println("tried to get attibutes");

        System.out.println("Authorization denied: " + failures);
        System.out.println("tried to log failures");


        System.out.println(
                "getAuthentication: " + failures.getAuthentication());
        System.out.println("tried to get authentication");


        System.out.println(
                "getAuthorization: " + failures.getAuthentication().get());

        System.out.println("tried to get authentication.get()");

        System.out.println("getPrincipal: " + failures.getAuthentication()
                .get()
                .getPrincipal());

        System.out.println("tried to get principal");

        if (failures.getAuthentication()
                .get()
                .getPrincipal()
                .equals("anonymousUser")) {
            return;
        }

        AppUserDTO details = (AppUserDTO) failures.getAuthentication()
                .get()
                .getPrincipal();
        System.out.println("tried to get details");

        AppUser user = details.getUser();
        System.out.println("tried to get user");

        String requestUri = null;
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            requestUri = request.getRequestURI(); // This is the URI
        }

        InfoSecEvent event = CreateInfoSecEvent.createEvent(requestUri,
                user.getEmail(),
                LoggingEvents.ACCESS_DENIED,
                requestUri);

        infoSecEventService.logEvent(event);

    }
}

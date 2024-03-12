package account.configuration;

import account.model.dto.data.AppUserDTO;
import account.model.entity.AppUser;
import account.model.entity.InfoSecEvent;
import account.model.enums.LoggingEvents;
import account.repository.AppUserRepository;
import account.service.AppUserOperationsService;
import account.service.InfoSecEventService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

import static account.service.AppUserOperationsService.MAX_FAILED_ATTEMPTS;

@Component
public class AuthenticationEvents {
    private final AppUserRepository appUserRepository;

    private final InfoSecEventService infoSecEventService;
    private final AppUserOperationsService appUserOperationsService;

    @Autowired
    public AuthenticationEvents(
            AppUserRepository appUserRepository,
            InfoSecEventService infoSecEventService,
            AppUserOperationsService appUserOperationsService) {
        this.appUserRepository = appUserRepository;
        this.infoSecEventService = infoSecEventService;
        this.appUserOperationsService = appUserOperationsService;
    }


    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {


        AppUserDTO details = (AppUserDTO) success.getAuthentication()
                .getPrincipal();
        AppUser user = details.getUser();

        if (user.getFailedAttempt() > 0) {
            appUserOperationsService.resetFailedAttempts(user);
        }


    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failures) {

        System.out.println("Authentication failed entrance");
        System.out.println("failures: " + failures);
        System.out.println(
                "failures.getAuthentication: " + failures.getAuthentication());
        System.out.println("failures.getAuthentication.getPrincipal: " +
                           failures.getAuthentication().getPrincipal());
        System.out.println(failures.getException().getMessage());

        if (failures.getAuthentication()
                .getPrincipal()
                .equals("anonymousUser")) {
            return;
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestUri = null;
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            requestUri = request.getRequestURI(); // This is the URI
        }

        String email = failures.getAuthentication().getName();
        AppUser user = appUserRepository.findAppUserByEmail(email).orElse(null);


        InfoSecEvent event = createInfoSecEvent(requestUri,
                email, LoggingEvents.LOGIN_FAILED,
                requestUri);

        System.out.println("Do I ever come here?");
        System.out.println("is user null? " + (user == null));

        if (user == null | (user != null && user.isAccountNonLocked())) {
            infoSecEventService.logEvent(event);
        }


        if (user != null) {
            if (user.isAccountNonLocked()) {

                appUserOperationsService.increaseFailedAttempts(user);

                int failedAttempts = user.getFailedAttempt() +
                                     1; // be aware that this is still
                // old user object

                if (failedAttempts >= MAX_FAILED_ATTEMPTS) {


                    user.setFailedAttempt(failedAttempts);
                    appUserOperationsService.lockUserAccount(user);

                    InfoSecEvent bruteForceEvent = createInfoSecEvent(
                            requestUri,
                            user.getEmail(), LoggingEvents.BRUTE_FORCE,
                            requestUri);

                    InfoSecEvent accountLockEvent = createInfoSecEvent(
                            requestUri,
                            user.getEmail(), LoggingEvents.LOCK_USER,
                            "Lock user ".concat(user.getEmail()));

                    infoSecEventService.logEvent(bruteForceEvent);
                    infoSecEventService.logEvent(accountLockEvent);

                }
            }


        }


    }

    private InfoSecEvent createInfoSecEvent(String path,
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

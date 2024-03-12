package account.service;

import account.exception.DataNotFoundException;
import account.exception.PasswordBreachedException;
import account.exception.PasswordIsSameAsOldException;
import account.exception.UserAlreadyExistsException;
import account.model.dto.data.AppUserDTO;
import account.model.dto.requests.SignupRequestDTO;
import account.model.dto.requests.UpdateUserRolesRequestDTO;
import account.model.dto.responses.SimplifiedUserDTO;
import account.model.entity.*;
import account.model.enums.*;
import account.model.record.DeleteUserResponse;
import account.model.record.LockUnlockUserRequest;
import account.model.record.LockUnlockUserResponse;
import account.repository.AppUserRepository;
import account.repository.BreachedPasswordRepository;
import account.repository.GroupRepository;
import account.repository.RoleRepository;
import account.utils.CreateInfoSecEvent;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class AppUserService implements UserDetailsService {


    private final AppUserRepository userDetailsRepository;
    private final PasswordEncoder passwordEncoder;
    private final BreachedPasswordRepository breachedPasswordRepository;
    private final GroupRepository groupRepository;
    private final RoleRepository roleRepository;
    private final InfoSecEventService infoSecEventService;

    public AppUserService(AppUserRepository userDetailsRepository,
                          PasswordEncoder passwordEncoder,
                          BreachedPasswordRepository breachedPasswordRepository,
                          GroupRepository groupRepository,
                          RoleRepository roleRepository,
                          InfoSecEventService infoSecEventService) {
        this.userDetailsRepository = userDetailsRepository;
        this.passwordEncoder = passwordEncoder;
        this.breachedPasswordRepository = breachedPasswordRepository;
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.infoSecEventService = infoSecEventService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws
            UsernameNotFoundException {

        Optional<AppUser> user = userDetailsRepository.findAppUserByEmail(
                username.toLowerCase());
        user.orElseThrow(
                () -> new UsernameNotFoundException("Not found: " + username));
        return new AppUserDTO(user.get());


    }


    public AppUser saveUser(SignupRequestDTO user) {
        Optional<AppUser> userExists = userDetailsRepository.findAppUserByEmail(
                user.getEmail());
        boolean isBreached = isPasswordBreached(user.getPassword());

        if (isBreached) {
            throw new PasswordBreachedException(
                    "The password is in the hacker's database!");
        }

        if (userExists.isPresent()) {
            throw new UserAlreadyExistsException("User exist!");
        }

        boolean isItFirstUser = userDetailsRepository.countAppUsers() == 0;
        AppUser appUser = new AppUser();
        appUser.setEmail(user.getEmail());
        appUser.setPassword(passwordEncoder.encode(user.getPassword()));
        appUser.setName(user.getName());
        appUser.setLastname(user.getLastname());
        appUser.setAccountNonLocked(true);
        appUser.setFailedAttempt(0);
        if (isItFirstUser) {
            appUser.setRoles(Set.of(getRole(
                    String.valueOf(UserRoles.ROLE_ADMINISTRATOR))));
            appUser.setGroup(getGroup(UserGroupCodes.ADMIN));
        } else {
            appUser.setRoles(
                    Set.of(getRole(String.valueOf(UserRoles.ROLE_USER))));
            appUser.setGroup(getGroup(UserGroupCodes.BUSINESS));
        }

        InfoSecEvent event = CreateInfoSecEvent.createEvent(
                "api/auth/signup", "Anonymous", LoggingEvents.CREATE_USER,
                appUser.getEmail());
        infoSecEventService.logEvent(event);
        return userDetailsRepository.save(appUser);
    }

    public List<SimplifiedUserDTO> getAllUsers() {
        List<SimplifiedUserDTO> userList = new ArrayList<>();

        Iterable<AppUser> allUsers = userDetailsRepository.findAll();

        for (AppUser user : allUsers) {

            SimplifiedUserDTO simplifiedUserDTO = SimplifiedUserDTO.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .lastname(user.getLastname())
                    .id(user.getId())
                    .roles(getUserRoleNames(user.getRoles()))
                    .build();
            userList.add(simplifiedUserDTO);
        }
        userList.forEach(user -> System.out.println(user.toString()));
        return userList;
    }

    public boolean isPasswordBreached(String password) {
        List<String> breachedPasswords = new ArrayList<>(
                List.of("PasswordForJanuary", "PasswordForFebruary",
                        "PasswordForMarch", "PasswordForApril",
                        "PasswordForMay", "PasswordForJune", "PasswordForJuly",
                        "PasswordForAugust",
                        "PasswordForSeptember", "PasswordForOctober",
                        "PasswordForNovember", "PasswordForDecember"));

//        Optional<BreachedPassword> isBreached = breachedPasswordRepository.findByPassword(password);
//        return isBreached.isPresent();
        return breachedPasswords.contains(password);
    }

    public void saveBreachedPassword(String password) {
        BreachedPassword breachedPassword = new BreachedPassword();
        breachedPassword.setPassword(password);
        breachedPasswordRepository.save(breachedPassword);
    }

    public boolean isPasswordSameAsOld(String oldPassword, String newPassword) {
        return passwordEncoder.matches(newPassword, oldPassword);
    }

    public void changePassword(AppUser user, String newPassword) {
        if (isPasswordBreached(newPassword)) {
            throw new PasswordBreachedException(
                    "The password is in the hacker's database!");
        }
        if (isPasswordSameAsOld(user.getPassword(), newPassword))
            throw new PasswordIsSameAsOldException(
                    "The passwords must be different!");

        user.setPassword(passwordEncoder.encode(newPassword));
        userDetailsRepository.save(user);
        InfoSecEvent event = CreateInfoSecEvent.createEvent(
                "api/auth/changepass", user.getEmail(),
                LoggingEvents.CHANGE_PASSWORD,
                user.getEmail());
        infoSecEventService.logEvent(event);
    }


    public Group getGroup(UserGroupCodes code) {
        Group group = groupRepository.findByCode(code);
        if (group == null) {
            throw new DataNotFoundException("Group not found");
        }
        return group;
    }

    public Role getRole(String name) {
        return roleRepository.findByName(
                (name)).orElseThrow(
                () -> new DataNotFoundException("Role not found"));
    }

    public List<String> getUserRoleNames(Set<Role> roles) {
        List<String> userRoles = new ArrayList<>();
        for (Role role : roles) {
            userRoles.add(role.getName());
        }
        Collections.sort(userRoles);
        return userRoles;
    }

    public DeleteUserResponse deleteUser(String email, String adminEmail) {
        Optional<AppUser> user = userDetailsRepository.findAppUserByEmail(
                email);
        if (user.isPresent()) {
            if (user.get().getGroup().getCode().equals(UserGroupCodes.ADMIN)) {
                throw new RuntimeException("Can't remove ADMINISTRATOR role!");
            }
            userDetailsRepository.delete(user.get());
            InfoSecEvent event = CreateInfoSecEvent.createEvent(
                    "api/admin/user", adminEmail,
                    LoggingEvents.DELETE_USER,
                    email
            );
            infoSecEventService.logEvent(event);
            return new DeleteUserResponse(email, "Deleted successfully!");

        }
        throw new UsernameNotFoundException("User not found!");
    }


    public SimplifiedUserDTO updateUserRoles(
            UpdateUserRolesRequestDTO request, String adminEmail) {
        Optional<AppUser> user = userDetailsRepository.findAppUserByEmail(
                request.getUser());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found!");
        }
        Role role =
                roleRepository.findByName("ROLE_".concat(request.getRole()))
                        .orElseThrow(
                                () -> new DataNotFoundException(
                                        "Role not found!"));

        Group group = groupRepository.findByRoleId(role.getId());


        boolean isRolePresent = user.get().getRoles().contains(role);
        OperationType operationType = request.getOperation();

        InfoSecEvent event = CreateInfoSecEvent.createEvent(
                "api/admin/user/role",
                adminEmail,
                operationType.equals(
                        OperationType.GRANT) ? LoggingEvents.GRANT_ROLE : LoggingEvents.REMOVE_ROLE,
                ""
        );

        if (operationType.equals(OperationType.GRANT)) {
            if (isRolePresent) {
                throw new RuntimeException("Role already present!");
            }
            if (!Objects.equals(user.get().getGroup().getId(), group.getId())) {
                throw new RuntimeException(
                        "The user cannot combine administrative and business roles!");
            }
            user.get().getRoles().add(role);
            event.setObject("Grant role ".concat((request.getRole()))
                    .concat(" to ")
                    .concat(user.get().getEmail()));
        } else { // REMOVE
            if (!isRolePresent) {
                throw new RuntimeException("The user does not have a role!");
            }
            if (user.get()
                    .getRoles()
                    .contains(getRole(String.valueOf(
                            UserRoles.ROLE_ADMINISTRATOR)))) {
                throw new RuntimeException("Can't remove ADMINISTRATOR role!");
            }

            if (user.get().getRoles().size() == 1) {
                throw new RuntimeException(
                        "The user must have at least one role!");
            }
            user.get().getRoles().remove(role);
            event.setObject("Remove role ".concat((request.getRole()))
                    .concat(" from ")
                    .concat(user.get().getEmail()));
        }


        AppUser updatedUser = userDetailsRepository.save(user.get());
        infoSecEventService.logEvent(event);

        return SimplifiedUserDTO.builder()
                .email(updatedUser.getEmail())
                .name(updatedUser.getName())
                .lastname(updatedUser.getLastname())
                .id(updatedUser.getId())
                .roles(getUserRoleNames(updatedUser.getRoles()))
                .build();
    }


    public LockUnlockUserResponse lockUnlockUser(LockUnlockUserRequest request,
                                                 String adminEmail) {
        Optional<AppUser> user = userDetailsRepository.findAppUserByEmail(
                request.user().toLowerCase());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found!");
        }

        LockUnlockOperation operation = request.operation();

        InfoSecEvent event = CreateInfoSecEvent.createEvent(
                "api/admin/user/access",
                adminEmail,
                operation.equals(
                        LockUnlockOperation.LOCK) ? LoggingEvents.LOCK_USER : LoggingEvents.UNLOCK_USER,
                "");

        if (operation.equals(LockUnlockOperation.LOCK)) {
            if (user.get().getGroup().getCode().equals(UserGroupCodes.ADMIN)) {
                throw new RuntimeException("Can't lock the ADMINISTRATOR!");
            }
            user.get().setAccountNonLocked(false);
            event.setObject("Lock user ".concat(user.get().getEmail()));
        } else {
            user.get().setAccountNonLocked(true);
            user.get().setFailedAttempt(0);
            event.setObject("Unlock user ".concat(user.get().getEmail()));
        }
        userDetailsRepository.save(user.get());
        infoSecEventService.logEvent(event);
        return new LockUnlockUserResponse("User ".concat(user.get().getEmail())
                .concat(" ")
                .concat(operation.equals(
                        LockUnlockOperation.LOCK) ? "locked!" : "unlocked!"));
    }

}
package account.configuration;

import account.model.entity.Group;
import account.model.entity.Role;
import account.model.enums.UserGroupCodes;
import account.model.enums.UserRoles;
import account.repository.GroupRepository;
import account.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Component
public class DataLoader {

    private final GroupRepository groupRepository;
    private final RoleRepository roleRepository;


    public DataLoader(GroupRepository groupRepository,
                      RoleRepository roleRepository) {
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        createRolesAndGroups();
    }


    private void createRolesAndGroups() {

        try {


            if (roleRepository.count() == 0) {
                Role adminRole = new Role(
                        String.valueOf(UserRoles.ROLE_ADMINISTRATOR));
                Role userRole = new Role(String.valueOf(UserRoles.ROLE_USER));
                Role accountantRole = new Role(
                        String.valueOf(UserRoles.ROLE_ACCOUNTANT));
                Role auditorRole = new Role(
                        String.valueOf(UserRoles.ROLE_AUDITOR));
                roleRepository.saveAll(
                        Arrays.asList(adminRole, userRole, accountantRole,
                                auditorRole));
            }

            if (groupRepository.count() ==
                0) {
                Optional<Role> adminRole = roleRepository.findByName(
                        String.valueOf(UserRoles.ROLE_ADMINISTRATOR));
                Optional<Role> userRole = roleRepository.findByName(
                        String.valueOf(UserRoles.ROLE_USER));
                Optional<Role> accountantRole = roleRepository.findByName(
                        String.valueOf(UserRoles.ROLE_ACCOUNTANT));
                Optional<Role> auditorRole = roleRepository.findByName(
                        String.valueOf(UserRoles.ROLE_AUDITOR));

                Group adminGroup = new Group();
                adminGroup.setName("Administrative");
                adminGroup.setCode(UserGroupCodes.ADMIN);
                adminGroup.setRoles(
                        new HashSet<>(
                                Collections.singletonList(adminRole.get())));

                Group businessGroup = new Group();
                businessGroup.setName("Business Users");
                businessGroup.setCode(UserGroupCodes.BUSINESS);
                businessGroup.setRoles(new HashSet<>(
                        Arrays.asList(userRole.get(), accountantRole.get(),
                                auditorRole.get())));

                groupRepository.saveAll(
                        Arrays.asList(adminGroup, businessGroup));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating roles and groups");
        }
    }


}


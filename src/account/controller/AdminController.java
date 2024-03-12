package account.controller;

import account.model.dto.data.AppUserDTO;
import account.model.dto.requests.UpdateUserRolesRequestDTO;
import account.model.dto.responses.SimplifiedUserDTO;
import account.model.record.DeleteUserResponse;
import account.model.record.LockUnlockUserRequest;
import account.model.record.LockUnlockUserResponse;
import account.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {

    private final AppUserService appUserService;

    @Autowired
    public AdminController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PutMapping("/user/role")
    public ResponseEntity<SimplifiedUserDTO> updateUserRoles(
            Authentication authentication, @Valid @RequestBody
    UpdateUserRolesRequestDTO request) {
        AppUserDTO details = (AppUserDTO) authentication.getPrincipal();
        System.out.println("update user roles");
        System.out.println(request.getUser());
        System.out.println(request.getRole());
        System.out.println(request.getOperation());
        return ResponseEntity.ok(
                appUserService.updateUserRoles(request, details.getUsername()));

    }


    @DeleteMapping("/user/{email}")
    public ResponseEntity<DeleteUserResponse> deleteUser(
            Authentication authentication,
            @PathVariable String email) {
        AppUserDTO details = (AppUserDTO) authentication.getPrincipal();

        return ResponseEntity.ok(
                appUserService.deleteUser(email, details.getUsername()));
    }


    @GetMapping("/user/")
    public ResponseEntity<List<SimplifiedUserDTO>> getAllUsers() {
        System.out.println("get all users");

        return ResponseEntity.ok(appUserService.getAllUsers());
    }

    @PutMapping("/user/access")
    public ResponseEntity<LockUnlockUserResponse> updateUserAccess(
            Authentication authentication,
            @Valid @RequestBody
            LockUnlockUserRequest request) {
        AppUserDTO details = (AppUserDTO) authentication.getPrincipal();

        return ResponseEntity.ok(
                appUserService.lockUnlockUser(request, details.getUsername()));


    }
}

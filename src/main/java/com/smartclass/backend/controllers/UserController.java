package com.smartclass.backend.controllers;

import com.smartclass.backend.dto.UserResponse;
import com.smartclass.backend.entity.UserEntity;
import com.smartclass.backend.entity.enumsUserEntity.AccountStatus;
import com.smartclass.backend.entity.enumsUserEntity.Role;
import com.smartclass.backend.service.UserService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET semua user
    @GetMapping
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers()
                .stream()
                .map(this::toUserResponse)
                .toList();
    }

    // GET satu user berdasarkan ID
    @GetMapping("/{id_user}")
    public UserResponse getUserById(
            @PathVariable Long id_user
    ) {

        return toUserResponse(
                userService.getUserById(id_user)
        );
    }

    // CREATE user
    @PostMapping
    public UserResponse createUser(
            @RequestBody UserEntity user
    ) {

        return toUserResponse(
                userService.createUser(user)
        );
    }

    // UPDATE user
    @PutMapping("/{id_user}")
    public UserResponse updateUser(
            @PathVariable Long id_user,
            @RequestBody UserEntity user
    ) {

        return toUserResponse(
                userService.updateUser(
                        id_user,
                        user
                )
        );
    }

    // DELETE user
    @DeleteMapping("/{id_user}")
    public String deleteUser(
            @PathVariable Long id_user
    ) {

        userService.deleteUser(id_user);

        return "User berhasil dihapus";
    }

    // UPDATE role
    @PatchMapping("/{id_user}/role")
    public UserResponse updateRole(
            @PathVariable Long id_user,
            @RequestParam Role role
    ) {

        return toUserResponse(
                userService.updateRole(
                        id_user,
                        role
                )
        );
    }

    // UPDATE account status
    @PatchMapping("/{id_user}/account-status")
    public UserResponse updateAccountStatus(
            @PathVariable Long id_user,
            @RequestParam AccountStatus accountStatus
    ) {

        return toUserResponse(
                userService.updateAccountStatus(
                        id_user,
                        accountStatus
                )
        );
    }

    // Convert UserEntity menjadi UserResponse
    private UserResponse toUserResponse(
            UserEntity user
    ) {

        return new UserResponse(
                user.getId_user(),
                user.getFull_name(),
                user.getEmail(),
                user.getRole(),
                user.getAccount_status(),
                user.getProfile_picture(),
                user.getLast_login_at(),
                user.getCreated_at(),
                user.getUpdated_at()
        );
    }
}
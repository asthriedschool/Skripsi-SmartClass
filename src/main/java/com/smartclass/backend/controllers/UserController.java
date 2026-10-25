package com.smartclass.backend.controllers;

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
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    // GET satu user berdasarkan ID
    @GetMapping("/{id_user}")
    public UserEntity getUserById(
            @PathVariable Long id_user
    ) {
        return userService.getUserById(id_user);
    }

    // CREATE user
    @PostMapping
    public UserEntity createUser(
            @RequestBody UserEntity user
    ) {
        return userService.createUser(user);
    }

    // UPDATE user
    @PutMapping("/{id_user}")
    public UserEntity updateUser(
            @PathVariable Long id_user,
            @RequestBody UserEntity user
    ) {
        return userService.updateUser(id_user, user);
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
    public UserEntity updateRole(
            @PathVariable Long id_user,
            @RequestParam Role role
    ) {
        return userService.updateRole(
                id_user,
                role
        );
    }

    // UPDATE account status
    @PatchMapping("/{id_user}/account-status")
    public UserEntity updateAccountStatus(
            @PathVariable Long id_user,
            @RequestParam AccountStatus accountStatus
    ) {
        return userService.updateAccountStatus(
                id_user,
                accountStatus
        );
    }
}

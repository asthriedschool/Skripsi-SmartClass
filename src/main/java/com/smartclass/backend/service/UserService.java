package com.smartclass.backend.service;

import com.smartclass.backend.dto.RegisterRequest;
import com.smartclass.backend.entity.UserEntity;
import com.smartclass.backend.entity.enumsUserEntity.AccountStatus;
import com.smartclass.backend.entity.enumsUserEntity.Role;
import com.smartclass.backend.exception.UserAlreadyExistsException;
import com.smartclass.backend.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // REGISTER USER
    // =========================================================
    public void registerUser(RegisterRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Data register tidak boleh null"
            );
        }

        if (request.fullName() == null ||
                request.fullName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Nama lengkap wajib diisi"
            );
        }

        if (request.email() == null ||
                request.email().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Email wajib diisi"
            );
        }

        if (request.password() == null ||
                request.password().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Password wajib diisi"
            );
        }

        String email = request.email().trim();

        // Cek apakah email sudah digunakan
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(
                    "Email sudah terdaftar: " + email
            );
        }

        // Membuat user baru
        UserEntity user = new UserEntity();

        user.setFull_name(request.fullName().trim());
        user.setEmail(email);

        // Password harus di-hash menggunakan BCrypt
        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        // User yang melakukan register melalui endpoint ini
        // otomatis dibuat sebagai PETUGAS
        user.setRole(Role.PETUGAS);

        // Account baru otomatis ACTIVE
        user.setAccount_status(AccountStatus.ACTIVE);

        userRepository.save(user);
    }

    // =========================================================
    // LOGIN / SPRING SECURITY
    // =========================================================
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User dengan email " + email +
                                        " tidak ditemukan"
                        )
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .disabled(
                        user.getAccount_status() ==
                                AccountStatus.INACTIVE
                )
                .build();
    }

    // =========================================================
    // GET SEMUA USER
    // =========================================================
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    // =========================================================
    // GET USER BERDASARKAN ID
    // =========================================================
    public UserEntity getUserById(Long id_user) {

        if (id_user == null) {
            throw new IllegalArgumentException(
                    "ID user tidak boleh null"
            );
        }

        return userRepository.findById(id_user)
                .orElseThrow(() -> new RuntimeException(
                        "User dengan ID " + id_user +
                                " tidak ditemukan"
                ));
    }

    // =========================================================
    // CREATE USER
    // =========================================================
    public UserEntity createUser(UserEntity user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "Data user tidak boleh null"
            );
        }

        validateUser(user);

        String email = user.getEmail().trim();

        // Cek email unique
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(
                    "Email sudah terdaftar: " + email
            );
        }

        user.setEmail(email);

        // Password harus disimpan dalam bentuk hash
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        return userRepository.save(user);
    }

    // =========================================================
    // UPDATE USER
    // =========================================================
    public UserEntity updateUser(
            Long id_user,
            UserEntity userData
    ) {

        if (id_user == null) {
            throw new IllegalArgumentException(
                    "ID user tidak boleh null"
            );
        }

        if (userData == null) {
            throw new IllegalArgumentException(
                    "Data user tidak boleh null"
            );
        }

        UserEntity user = userRepository.findById(id_user)
                .orElseThrow(() -> new RuntimeException(
                        "User dengan ID " + id_user +
                                " tidak ditemukan"
                ));

        validateUser(userData);

        String email = userData.getEmail().trim();

        /*
         * Cek apakah email sudah digunakan oleh user lain.
         * Email milik user yang sedang di-update tetap diperbolehkan.
         */
        userRepository.findByEmail(email)
                .ifPresent(existingUser -> {

                    if (!existingUser.getId_user().equals(id_user)) {
                        throw new UserAlreadyExistsException(
                                "Email sudah digunakan oleh user lain: "
                                        + email
                        );
                    }
                });

        user.setFull_name(userData.getFull_name().trim());
        user.setEmail(email);

        /*
         * Jika password yang dikirim sudah berupa BCrypt hash,
         * jangan hash ulang.
         *
         * Ini mencegah masalah:
         * BCrypt(BCrypt(password))
         */
        user.setPassword(
                encodePasswordIfNeeded(userData.getPassword())
        );

        user.setRole(userData.getRole());
        user.setProfile_picture(userData.getProfile_picture());
        user.setAccount_status(userData.getAccount_status());
        user.setLast_login_at(userData.getLast_login_at());

        return userRepository.save(user);
    }

    // =========================================================
    // DELETE USER
    // =========================================================
    public void deleteUser(Long id_user) {

        if (id_user == null) {
            throw new IllegalArgumentException(
                    "ID user tidak boleh null"
            );
        }

        if (!userRepository.existsById(id_user)) {
            throw new RuntimeException(
                    "User dengan ID " + id_user +
                            " tidak ditemukan"
            );
        }

        userRepository.deleteById(id_user);
    }

    // =========================================================
    // UPDATE ROLE
    // =========================================================
    public UserEntity updateRole(
            Long id_user,
            Role role
    ) {

        UserEntity user = getUserById(id_user);

        if (role == null) {
            throw new IllegalArgumentException(
                    "Role tidak boleh null"
            );
        }

        user.setRole(role);

        return userRepository.save(user);
    }

    // =========================================================
    // UPDATE ACCOUNT STATUS
    // =========================================================
    public UserEntity updateAccountStatus(
            Long id_user,
            AccountStatus accountStatus
    ) {

        UserEntity user = getUserById(id_user);

        if (accountStatus == null) {
            throw new IllegalArgumentException(
                    "Account status tidak boleh null"
            );
        }

        user.setAccount_status(accountStatus);

        return userRepository.save(user);
    }

    // =========================================================
    // VALIDASI USER
    // =========================================================
    private void validateUser(UserEntity user) {

        if (user.getFull_name() == null ||
                user.getFull_name().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Nama lengkap wajib diisi"
            );
        }

        if (user.getEmail() == null ||
                user.getEmail().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Email wajib diisi"
            );
        }

        if (user.getPassword() == null ||
                user.getPassword().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Password wajib diisi"
            );
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException(
                    "Role wajib diisi"
            );
        }

        if (user.getAccount_status() == null) {
            throw new IllegalArgumentException(
                    "Account status wajib diisi"
            );
        }
    }

    // =========================================================
    // PASSWORD ENCODING
    // =========================================================
    private String encodePasswordIfNeeded(String password) {

        /*
         * BCrypt biasanya memiliki prefix:
         * $2a$
         * $2b$
         * $2y$
         *
         * Jika sudah hash, jangan hash lagi.
         */
        if (password.startsWith("$2a$") ||
                password.startsWith("$2b$") ||
                password.startsWith("$2y$")) {

            return password;
        }

        return passwordEncoder.encode(password);
    }
}

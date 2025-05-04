package com.library.service;

import com.library.dto.SignUpRequest;
import com.library.model.user.Role;
import com.library.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    User createUser(SignUpRequest signUpRequest);

    User registerCustomer(SignUpRequest signUpRequest, Long customerId);

    User registerEmployee(SignUpRequest signUpRequest, Long employeeId);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserById(Long id);

    Page<User> getAllUsers(Pageable pageable);

    Page<User> getUsersByRole(String roleName, Pageable pageable);

    User updateUser(User user);

    void deleteUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Role createRoleIfNotFound(String name);

    void changePassword(Long userId, String oldPassword, String newPassword);

    boolean verifyPassword(User user, String password);

    void toggleUserStatus(Long userId);

    Optional<User> getUserByCustomerId(Long customerId);

    Optional<User> getUserByEmployeeId(Long employeeId);
}
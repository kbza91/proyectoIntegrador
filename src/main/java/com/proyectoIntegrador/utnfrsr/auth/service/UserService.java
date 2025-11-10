package com.proyectoIntegrador.utnfrsr.auth.service;

import com.proyectoIntegrador.utnfrsr.auth.model.User;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getUsers();

    User findByUsername(String username);

    Optional<User> findUserById(Long id);

    User getUserRol(String authorizationHeader);

    void updateRoleToUser(Long id, String newRoleName) throws BadRequestException;

    void modifyPassword(String authorizationHeader, String newPassword);

    @Transactional
    void updateUser(Long id, User updatedUser);

    @Transactional
    void deleteUser(Long id);
}
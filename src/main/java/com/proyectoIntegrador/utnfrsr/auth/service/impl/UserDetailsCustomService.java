package com.proyectoIntegrador.utnfrsr.auth.service.impl;

import com.proyectoIntegrador.utnfrsr.auth.enums.Rol;
import com.proyectoIntegrador.utnfrsr.auth.exceptions.UserException;
import com.proyectoIntegrador.utnfrsr.auth.model.User;
import com.proyectoIntegrador.utnfrsr.auth.repository.UserRepository;
import com.proyectoIntegrador.utnfrsr.auth.service.UserService;
import com.proyectoIntegrador.utnfrsr.exceptions.DataAlreadyExistException;
import com.proyectoIntegrador.utnfrsr.exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsCustomService implements UserDetailsService, UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String BEARER = "Bearer ";

    private final Logger logger = LoggerFactory.getLogger(UserDetailsCustomService.class);

    public void register(User user) throws DataAlreadyExistException {
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria para el registro");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DataAlreadyExistException("El nombre de usuario ya existe");
        }

        User userEntity = new User();
        userEntity.setId(user.getId());
        userEntity.setUsername(user.getUsername());
        userEntity.setNombre(user.getNombre());
        userEntity.setApellido(user.getApellido());
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userEntity.setEmail((user.getEmail()));
        userEntity.setCreationDate(LocalDate.now().atStartOfDay());
        userEntity.setRol(user.getRol());
        userEntity.setTelefono(user.getTelefono());
        userEntity.setDireccion(user.getDireccion());

        String jwt = jwtUtils.generateJwt(user);

        user.setJwt(jwt);

        userRepository.save(userEntity);

    }

    @Transactional
    public User logIn(User user) throws NotFoundException {

        Optional<User> userFound = userRepository.findByEmail(user.getEmail());
        if (userFound.isEmpty()) {
            throw new NotFoundException("Usuario no encontrado");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!(passwordEncoder.matches(user.getPassword(), userFound.get().getPassword()))) {
            throw new NotFoundException("Contraseña incorrecta");
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userFound, null, userFound.get().getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        userFound.get().setIsSession(true);

        String jwt = jwtUtils.generateJwt(userFound.get());
        userFound.get().setJwt(jwt);


        return userFound.get();
    }

    public User userDataFetching(HttpServletRequest request) throws NotFoundException {

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtils.extractUsername(jwt);

            Optional<User> user = userRepository.findByUsername(username);
            if (Boolean.TRUE.equals(user.get().getIsSession())) {
                return user.get();
            } else {
                throw new NotFoundException("USUARIO EN SESIÓN NO ENCONTRADO O SESIÓN INACTIVA");
            }

        } else {
            throw new NotFoundException("TOKEN NO PROPORCIONADO");
        }
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> foundUser = userRepository.findByUsername(username);
        if (foundUser.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        return foundUser.get();
    }

    @Override
    public void updateRoleToUser(Long id, String newRoleName) throws BadRequestException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        Rol newRol;
        try {
            newRol = Rol.valueOf(newRoleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Rol no válido: " + newRoleName);
        }

        if (user.getRol() == newRol) {
            throw new BadRequestException("El usuario ya tiene asignado el rol " + newRol.name());
        }

        user.setRol(newRol);
        userRepository.save(user);

    }

    @Override
    @Transactional
    public User getUserRol(String authorizationHeader) {
        logger.info(authorizationHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtils.extractUsername(jwt);

            Optional<User> user = userRepository.findByUsername(username);
            if (Boolean.TRUE.equals(user.get().getIsSession())) {
                return user.get();
            } else {
                throw new UserException("USUARIO EN SESIÓN NO ENCONTRADO");
            }

        } else {
            throw new UserException("USUARIO EN SESIÓN NO ENCONTRADO");
        }
    }

    public void signOut(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtils.extractUsername(jwt);

            Optional<User> user = userRepository.findByUsername(username);
            if (Boolean.TRUE.equals(user.get().getIsSession())) {
                user.get().setIsSession(false);
                userRepository.save(user.get());
            } else {
                throw new UserException("USUARIO EN SESIÓN NO ENCONTRADO");
            }
        } else {
            throw new UserException("USUARIO EN SESIÓN NO ENCONTRADO");
        }
    }

    @Override
    public void modifyPassword(String authorizationHeader, String newPassword) {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtils.extractUsername(jwt);

            Optional<User> user = userRepository.findByUsername(username);
            if (!Boolean.TRUE.equals(user.get().getIsSession())) {
                throw new NotFoundException("Usuario no encontrado o sesión inactiva");
            }

            String encodedPassword = passwordEncoder.encode(newPassword);
            user.get().setPassword(encodedPassword);
            userRepository.save(user.get());
        } else {
            throw new NotFoundException("Token no proporcionado");
        }
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findByDeletedFalse();
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + username));
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    @Override
    public void updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario con ID " + id + " no encontrado"));

        if (!existingUser.getUsername().equals(updatedUser.getUsername())) {
            if (userRepository.findByUsername(updatedUser.getUsername()).isPresent()) {
                throw new RuntimeException("El nombre de usuario ya está en uso");
            }
            existingUser.setUsername(updatedUser.getUsername());
        }

        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
                throw new RuntimeException("El email ya está registrado");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }

        existingUser.setNombre(updatedUser.getNombre());
        existingUser.setApellido(updatedUser.getApellido());
        existingUser.setTelefono(updatedUser.getTelefono());
        existingUser.setDireccion(updatedUser.getDireccion());

        if (updatedUser.getRol() != null) {
            existingUser.setRol(updatedUser.getRol());
        }

        userRepository.save(existingUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (Boolean.TRUE.equals(user.isDeleted())) {
            throw new NotFoundException("El usuario ya está eliminado");
        }

        user.setDeleted(true);
        userRepository.save(user);
    }
}
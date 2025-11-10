package com.proyectoIntegrador.utnfrsr.auth.controller;

import com.proyectoIntegrador.utnfrsr.auth.model.User;
import com.proyectoIntegrador.utnfrsr.auth.service.impl.UserDetailsCustomService;
import com.proyectoIntegrador.utnfrsr.exceptions.DataAlreadyExistException;
import com.proyectoIntegrador.utnfrsr.exceptions.NotFoundException;
import com.proyectoIntegrador.utnfrsr.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class UserAuthController {

    private final UserDetailsCustomService userDetailsCustomService;

    UserDetailsService userDetailsService;

    public UserAuthController(UserDetailsCustomService userDetailsCustomService, UserDetailsService userDetailsService){
        this.userDetailsCustomService = userDetailsCustomService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> userData(HttpServletRequest request) {

        return new ResponseEntity<>(userDetailsCustomService.userDataFetching(request), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> logIn(@RequestBody User user){

        try {
            User response = userDetailsCustomService.logIn(user);

            if (Boolean.TRUE.equals(response.isDeleted())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("El usuario ha sido eliminado y no puede iniciar sesión.");
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .header("Authorization", "Bearer " + response.getJwt())
                    .body(response);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Autenticación fallida: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al procesar la autenticación: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody User user)
            throws DataAlreadyExistException {
        try {
            userDetailsCustomService.register(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Usuario registrado", HttpStatus.CREATED.value()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al crear el usuario: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping
    public String login(){
        return "login";
    }

    @GetMapping("/userRol")
    public ResponseEntity<User> getUserRol(@RequestHeader("Authorization") String authorizationHeader){
        return ResponseEntity.status(HttpStatus.OK).body(userDetailsCustomService.getUserRol(authorizationHeader));
    }

    @PostMapping("/signOut")
    public ResponseEntity<User> signOut(@RequestHeader("Authorization") String authorizationHeader){
        userDetailsCustomService.signOut(authorizationHeader);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/modifyPassword")
    public ResponseEntity<String> modifyPassword(@RequestHeader("Authorization") String authorizationHeader, @RequestBody User user) {
        try {
            userDetailsCustomService.modifyPassword(authorizationHeader, user.getPassword());
            return ResponseEntity.ok("Contraseña actualizada correctamente");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userDetailsCustomService.getUsers();
        return ResponseEntity.ok(users);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost:5500", "http://127.0.0.1:63342", "http://localhost:63342")); // tu front
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User updatedUser) {

        try {
            userDetailsCustomService.updateUser(id, updatedUser);
            return ResponseEntity.ok(
                    new ApiResponse("Usuario actualizado correctamente", HttpStatus.OK.value())
            );
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Error: " + e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al actualizar usuario: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        try {
            userDetailsCustomService.deleteUser(id);
            return ResponseEntity.ok(new ApiResponse("Usuario eliminado correctamente", HttpStatus.OK.value()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al eliminar usuario: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
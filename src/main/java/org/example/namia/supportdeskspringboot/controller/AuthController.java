package org.example.namia.supportdeskspringboot.controller;

import org.example.namia.supportdeskspringboot.exception.EmailAlreadyExistsException;
import org.example.namia.supportdeskspringboot.exception.ResourceNotFoundException;
import org.example.namia.supportdeskspringboot.model.dto.request.LoginRequest;
import org.example.namia.supportdeskspringboot.model.dto.request.RegisterRequest;
import org.example.namia.supportdeskspringboot.model.dto.response.JwtResponse;
import org.example.namia.supportdeskspringboot.model.entity.User;
import org.example.namia.supportdeskspringboot.model.enums.Roles;
import org.example.namia.supportdeskspringboot.model.shared.Success;
import org.example.namia.supportdeskspringboot.repository.UserRepository;
import org.example.namia.supportdeskspringboot.security.JwtUtils;
import org.example.namia.supportdeskspringboot.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils,
            PasswordEncoder passwordEncoder,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Success<User>> registerUser(@RequestBody RegisterRequest registerRequest) {

        userService.findByEmail(registerRequest.email()).ifPresent(user -> {
            throw new EmailAlreadyExistsException(
                    "Email is already registered: " + registerRequest.email());
        });


        User user = new User();
        user.setEmail(registerRequest.email());

        user.setPasswordHash(passwordEncoder.encode(registerRequest.password()));
        user.setRole(registerRequest.role());

        userService.Create(user);

        return ResponseEntity.ok(new Success<>(
                user,
                "User registered successfully!"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );

        User user = userService.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NOT FOUND",
                        "User not found"
                ));

        String jwt = jwtUtils.generateToken(authentication.getName());
        return ResponseEntity.ok(new JwtResponse(jwt, user));
    }

    //REFRESH, LOG OUT LEFT
}

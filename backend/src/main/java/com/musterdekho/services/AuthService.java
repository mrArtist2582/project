package com.musterdekho.services;

import com.musterdekho.dtos.AuthResponse;
import com.musterdekho.dtos.LoginRequest;
import com.musterdekho.dtos.RegisterRequest;
import com.musterdekho.models.User;
import com.musterdekho.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent() ||
                userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with given username or email");
        }
        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .build();
        userRepository.save(newUser);

        String token = jwtService.generateToken(
                org.springframework.security.core.userdetails.User.withUsername(newUser.getUsername())
                        .password(newUser.getPassword()).authorities().build());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
            );
            org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authenticate.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            return new AuthResponse(token);
        } catch (AuthenticationException ex) {
            throw new RuntimeException("Invalid credentials");
        }
    }
}

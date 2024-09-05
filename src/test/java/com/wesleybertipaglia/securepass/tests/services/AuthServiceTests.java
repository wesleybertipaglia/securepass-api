package com.wesleybertipaglia.securepass.tests.services;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.wesleybertipaglia.securepass.records.auth.SignInRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignInResponseRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpResponseRecord;
import com.wesleybertipaglia.securepass.repositories.UserRepository;
import com.wesleybertipaglia.securepass.services.auth.AuthService;
import com.wesleybertipaglia.securepass.entities.User;

public class AuthServiceTests {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSignInSuccessfully() {
        String email = "test@example.com";
        String rawPassword = "password123";
        String encodedPassword = "$2a$10$eW5lCfRMl5Hftg6woBQaWe";
        UUID userId = UUID.randomUUID();

        User user = new User("Test User", email, encodedPassword);
        user.setId(userId);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("mockedToken");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        SignInRequestRecord signInRequest = new SignInRequestRecord(email, rawPassword);
        SignInResponseRecord response = authService.signIn(signInRequest);

        assertEquals("mockedToken", response.accessToken());
        assertEquals(86400000, response.expiresIn());
    }

    @Test
    void shouldThrowExceptionWhenEmailNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        SignInRequestRecord signInRequest = new SignInRequestRecord("test@example.com", "password");
        assertThrows(BadCredentialsException.class, () -> authService.signIn(signInRequest));
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        User user = new User("Test User", "test@example.com", "$2a$10$eW5lCfRMl5Hftg6woBQaWe");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        SignInRequestRecord signInRequest = new SignInRequestRecord("test@example.com", "wrongpassword");
        assertThrows(BadCredentialsException.class, () -> authService.signIn(signInRequest));
    }

    @Test
    void shouldSignUpSuccessfully() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        SignUpRequestRecord signUpRequest = new SignUpRequestRecord("Test User", "test@example.com", "password123");
        User user = new User(signUpRequest.name(), signUpRequest.email(), "encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        SignUpResponseRecord response = authService.signUp(signUpRequest);

        assertEquals(signUpRequest.name(), response.name());
        assertEquals(signUpRequest.email(), response.email());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        SignUpRequestRecord signUpRequest = new SignUpRequestRecord("Test User", "test@example.com", "password123");

        assertThrows(BadCredentialsException.class, () -> authService.signUp(signUpRequest));
    }

    @Test
    void shouldDeleteAccountSuccessfully() {
        UUID userId = UUID.randomUUID();
        User user = new User("Test User", "test@example.com", "encodedPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);
        authService.deleteAccount(userId.toString());
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThrows(BadCredentialsException.class, () -> authService.deleteAccount(UUID.randomUUID().toString()));
    }

}

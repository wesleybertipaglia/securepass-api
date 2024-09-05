package com.wesleybertipaglia.securepass.tests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

public class AuthServiceUnitTests {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$eW5lCfRMl5Hftg6woBQaWe";
    private static final UUID USER_ID = UUID.randomUUID();

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
        // arrange
        User user = new User("Test User", TEST_EMAIL, ENCODED_PASSWORD);
        user.setId(USER_ID);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("mockedToken");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        SignInRequestRecord signInRequest = new SignInRequestRecord(TEST_EMAIL, TEST_PASSWORD);

        // act
        SignInResponseRecord response = authService.signIn(signInRequest);

        // assert
        assertEquals("mockedToken", response.accessToken());
        assertEquals(86400000, response.expiresIn());
    }

    @Test
    void shouldThrowExceptionWhenEmailNotFound() {
        // arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        SignInRequestRecord signInRequest = new SignInRequestRecord(TEST_EMAIL, "password");

        // act & assert
        assertThrows(BadCredentialsException.class, () -> authService.signIn(signInRequest));
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        // arrange
        User user = new User("Test User", TEST_EMAIL, ENCODED_PASSWORD);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        SignInRequestRecord signInRequest = new SignInRequestRecord(TEST_EMAIL, "wrongpassword");

        // act & assert
        assertThrows(BadCredentialsException.class, () -> authService.signIn(signInRequest));
    }

    @Test
    void shouldSignUpSuccessfully() {
        // arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        SignUpRequestRecord signUpRequest = new SignUpRequestRecord("Test User", TEST_EMAIL, TEST_PASSWORD);
        User user = new User(signUpRequest.name(), signUpRequest.email(), "encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // act
        SignUpResponseRecord response = authService.signUp(signUpRequest);

        // assert
        assertEquals(signUpRequest.name(), response.name());
        assertEquals(signUpRequest.email(), response.email());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        SignUpRequestRecord signUpRequest = new SignUpRequestRecord("Test User", TEST_EMAIL, TEST_PASSWORD);

        // act & assert
        assertThrows(BadCredentialsException.class, () -> authService.signUp(signUpRequest));
    }

    @Test
    void shouldDeleteAccountSuccessfully() {
        // arrange
        User user = new User("Test User", TEST_EMAIL, "encodedPassword");
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        // act
        authService.deleteAccount(USER_ID.toString());

        // assert
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        // arrange
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // act & assert
        assertThrows(BadCredentialsException.class, () -> authService.deleteAccount(UUID.randomUUID().toString()));
    }

}

package com.wesleybertipaglia.securepass.services.auth;

import com.wesleybertipaglia.securepass.entities.User;
import com.wesleybertipaglia.securepass.records.auth.SignInRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignInResponseRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpResponseRecord;
import com.wesleybertipaglia.securepass.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_NAME = "Test";
    private static final String USER_EMAIL = "test@example.com";
    private static final String USER_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$eW5lCfRMl5Hftg6woBQaWe";
    private static final String TOKEN_MOCKED = "mockedToken";
    private static final long TOKEN_EXPIRATION_IN = 86400000;

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private  Jwt jwt;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Should sign in successfully")
    void shouldSignInSuccessfully() {
        // arrange
        User user = new User(USER_ID, USER_NAME, USER_EMAIL, ENCODED_PASSWORD);
        SignInRequestRecord signInRequest = new SignInRequestRecord(USER_EMAIL, USER_PASSWORD);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(USER_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwt.getTokenValue()).thenReturn(TOKEN_MOCKED);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // act
        SignInResponseRecord response = authService.signIn(signInRequest);

        // assert
        assertEquals(TOKEN_MOCKED, response.accessToken(), "Access token should match");
        assertEquals(TOKEN_EXPIRATION_IN, response.expiresIn(), "Expiration time should match");
    }

    @Test
    @DisplayName("Should throw exception when email not found")
    void shouldThrowExceptionWhenEmailNotFound() {
        // arrange
        SignInRequestRecord signInRequest = new SignInRequestRecord(USER_EMAIL, USER_PASSWORD);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(BadCredentialsException.class, () -> authService.signIn(signInRequest),
                "Should throw BadCredentialsException when email is not found");
    }

    @Test
    @DisplayName("Should throw exception when password does not match")
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        // arrange
        User user = new User(USER_ID, USER_NAME, USER_EMAIL, ENCODED_PASSWORD);
        SignInRequestRecord signInRequest = new SignInRequestRecord(USER_EMAIL, "wrongpassword");
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // act & assert
        assertThrows(BadCredentialsException.class, () -> authService.signIn(signInRequest),
                "Should throw BadCredentialsException when password does not match");
    }

    @Test
    @DisplayName("Should sign up successfully")
    void shouldSignUpSuccessfully() {
        // arrange
        User savedUser = new User(USER_ID, USER_NAME, USER_EMAIL, "encodedPassword");
        SignUpRequestRecord signUpRequest = new SignUpRequestRecord(USER_NAME, USER_EMAIL, USER_PASSWORD);
        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(USER_PASSWORD)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // act
        SignUpResponseRecord response = authService.signUp(signUpRequest);

        // assert
        assertEquals(USER_NAME, response.name(), "Name should match");
        assertEquals(USER_EMAIL, response.email(), "Email should match");
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // arrange
        SignUpRequestRecord signUpRequest = new SignUpRequestRecord(USER_NAME, USER_EMAIL, USER_PASSWORD);
        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(true);

        // act & assert
        assertThrows(BadCredentialsException.class, () -> authService.signUp(signUpRequest),
                "Should throw BadCredentialsException when email already exists");
    }

    @Test
    @DisplayName("Should delete account successfully")
    void shouldDeleteAccountSuccessfully() {
        // arrange
        User user = new User(USER_ID, USER_NAME, USER_EMAIL, ENCODED_PASSWORD);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        // act
        authService.deleteAccount(USER_ID.toString());

        // assert
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("Should throw exception when account not found")
    void shouldThrowExceptionWhenAccountNotFound() {
        // arrange
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // act & assert
        assertThrows(BadCredentialsException.class, () -> authService.deleteAccount(UUID.randomUUID().toString()),
                "Should throw BadCredentialsException when account is not found");
    }
}
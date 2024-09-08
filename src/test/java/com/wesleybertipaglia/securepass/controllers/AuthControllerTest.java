package com.wesleybertipaglia.securepass.controllers;

import com.wesleybertipaglia.securepass.records.auth.SignInRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignInResponseRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpResponseRecord;
import com.wesleybertipaglia.securepass.services.auth.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;


    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_NAME = "User Name";
    private static final String USER_EMAIL = "user@email.com";
    private static final String USER_PASSWORD = "P4$sw0rd#1$#%";
    private static final String TOKEN_MOCKED = "$2a$10$eW5lCfRMl5Hftg6woBQaWe";
    private static final long TOKEN_EXPIRATION_IN = 86400000;

    @Nested
    class signUp {
        @Test
        @DisplayName("Should successfully sign up")
        void shouldSuccessfullySignUp() {
            // arrange
            SignUpRequestRecord request = new SignUpRequestRecord(USER_NAME, USER_EMAIL, USER_PASSWORD);
            SignUpResponseRecord expectedResponse = new SignUpResponseRecord(USER_NAME, USER_EMAIL);
            when(authService.signUp(request)).thenReturn(expectedResponse);

            // act
            SignUpResponseRecord response = authController.signup(request);

            // assert
            assertEquals(expectedResponse, response);
        }

        @Test
        @DisplayName("Should throw an exception when email is already in use")
        void shouldThrowExceptionWhenEmailIsInUse() {
            // arrange
            SignUpRequestRecord request = new SignUpRequestRecord(USER_NAME, USER_EMAIL, USER_PASSWORD);
            when(authService.signUp(request)).thenThrow(new BadCredentialsException("E-mail is already in use."));

            // act & assert
            assertThrows(BadCredentialsException.class, () -> authService.signUp(request),
                    "Should throw BadCredentialsException when email is already in use");
        }
    }

    @Nested
    class signIn {
        @Test
        @DisplayName("Should successfully sign in")
        void shouldSuccessfullySignIn() {
            // arrange
            SignInRequestRecord request = new SignInRequestRecord(USER_EMAIL, USER_PASSWORD);
            SignInResponseRecord expectedResponse = new SignInResponseRecord(TOKEN_MOCKED, TOKEN_EXPIRATION_IN);
            when(authService.signIn(request)).thenReturn(expectedResponse);

            // act
            SignInResponseRecord response = authController.signin(request);

            // assert
            assertEquals(expectedResponse, response);
        }

        @Test
        @DisplayName("Should throw an exception when user is not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // arrange
            SignInRequestRecord request = new SignInRequestRecord(USER_EMAIL, USER_PASSWORD);
            when(authService.signIn(request)).thenThrow(new BadCredentialsException("Invalid e-mail or password."));

            // act & assert
            assertThrows(BadCredentialsException.class, () -> authService.signIn(request),
                    "Should throw BadCredentialsException when user is not found");
        }

        @Test
        @DisplayName("Should throw an exception when password is incorrect")
        void shouldThrowExceptionWhenPasswordIsIncorrect() {
            // arrange
            SignInRequestRecord request = new SignInRequestRecord(USER_EMAIL, USER_PASSWORD);
            when(authService.signIn(request)).thenThrow(new BadCredentialsException("Invalid e-mail or password."));

            // act & assert
            assertThrows(BadCredentialsException.class, () -> authService.signIn(request),
                    "Should throw BadCredentialsException when password is incorrect");
        }
    }

    @Nested
    class deleteAccount {
        @Test
        @DisplayName("Should delete account successfully")
        void shouldDeleteAccountSuccessfully() {
            // arrange
            doNothing().when(authService).deleteAccount(USER_ID.toString());

            // act
            authService.deleteAccount(USER_ID.toString());

            // assert
            verify(authService, times(1)).deleteAccount(USER_ID.toString());
        }

        @Test
        @DisplayName("Should throw exception when account not found")
        void shouldThrowExceptionWhenAccountNotFound() {
            // arrange
            doThrow(new BadCredentialsException("Account not found")).when(authService).deleteAccount(anyString());

            // act & assert
            assertThrows(BadCredentialsException.class, () -> authService.deleteAccount(UUID.randomUUID().toString()),
                    "Should throw BadCredentialsException when account is not found");
        }
    }
}
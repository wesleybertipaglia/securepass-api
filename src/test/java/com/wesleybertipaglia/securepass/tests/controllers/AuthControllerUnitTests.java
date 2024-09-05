package com.wesleybertipaglia.securepass.tests.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.wesleybertipaglia.securepass.controllers.AuthController;
import com.wesleybertipaglia.securepass.records.auth.SignInRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignInResponseRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpResponseRecord;
import com.wesleybertipaglia.securepass.services.auth.AuthService;

public class AuthControllerUnitTests {
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private JwtAuthenticationToken mockToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // mock access token
        mockToken = mock(JwtAuthenticationToken.class);
        when(mockToken.getName()).thenReturn("user-id");
    }

    // sign in
    @Test
    void shouldSuccessfullySignIn() throws Exception {
        // arrange
        SignInRequestRecord request = new SignInRequestRecord("test-username", "test-password");
        SignInResponseRecord expectedResponse = new SignInResponseRecord("test-token", 86400L);

        when(authService.signIn(eq(request))).thenReturn(expectedResponse);

        // act
        SignInResponseRecord response = authController.signin(request);

        // assert
        assertEquals(expectedResponse, response);
    }

    // sign up
    @Test
    void shouldSuccessfullySignUp() throws Exception {
        // arrange
        SignUpRequestRecord request = new SignUpRequestRecord("test-name", "test@email.com", "test-password");
        SignUpResponseRecord expectedResponse = new SignUpResponseRecord("test-name", "test@email.com");

        when(authService.signUp(eq(request))).thenReturn(expectedResponse);

        // act
        SignUpResponseRecord response = authController.signup(request);

        // assert
        assertEquals(expectedResponse, response);
    }

}

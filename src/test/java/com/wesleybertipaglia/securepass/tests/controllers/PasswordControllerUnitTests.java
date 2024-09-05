package com.wesleybertipaglia.securepass.tests.controllers;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.data.domain.Page;

import com.wesleybertipaglia.securepass.controllers.PasswordController;
import com.wesleybertipaglia.securepass.records.password.PasswordRequestRecord;
import com.wesleybertipaglia.securepass.records.password.PasswordResponseRecord;
import com.wesleybertipaglia.securepass.services.password.PasswordService;

public class PasswordControllerUnitTests {

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private PasswordController passwordController;

    private JwtAuthenticationToken mockToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // mock access token
        mockToken = mock(JwtAuthenticationToken.class);
        when(mockToken.getName()).thenReturn("user-id");
    }

    // create password
    @Test
    void shouldSuccessfullyCreatePassword() throws Exception {
        // arrange
        UUID passwordId = UUID.randomUUID();
        PasswordRequestRecord request = new PasswordRequestRecord("test-label", "test-password");
        PasswordResponseRecord expectedResponse = new PasswordResponseRecord(passwordId, "test-label", "test-password",
                null);

        when(passwordService.createPassword(eq(request), eq("user-id")))
                .thenReturn(expectedResponse);

        // act
        ResponseEntity<PasswordResponseRecord> response = passwordController.createPassword(request, mockToken);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be 200");
        assertEquals(expectedResponse, response.getBody(), "Response body should be equals to expectedResponse");
        verify(passwordService).createPassword(request, "user-id");
    }

    @Test
    void shouldFailCreatePassword() throws Exception {
        // arrange
        PasswordRequestRecord request = new PasswordRequestRecord("test-label", null);

        // act
        ResponseEntity<PasswordResponseRecord> response = passwordController.createPassword(request, mockToken);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be 200");
        assertEquals(null, response.getBody(), "Response body should be null");
        verify(passwordService).createPassword(request, "user-id");
    }

    // list passwords
    @Test
    void shouldSuccessfullyListPasswords() throws Exception {
        // arrange
        Page<PasswordResponseRecord> emptyPage = Page.empty();
        when(passwordService.listPasswords(0, 10, "user-id")).thenReturn(emptyPage);

        // act
        ResponseEntity<Page<PasswordResponseRecord>> response = passwordController.listPasswords(0, 10, mockToken);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be 200");
        assertEquals(emptyPage, response.getBody(), "Response body should be an empty page");
        verify(passwordService).listPasswords(0, 10, "user-id");
    }

    // get password
    @Test
    void shouldSuccessfullyGetPassword() throws Exception {
        // arrange
        UUID passwordId = UUID.randomUUID();
        PasswordResponseRecord expectedResponse = new PasswordResponseRecord(passwordId, "test-label", "test-password",
                null);

        when(passwordService.getPassword(eq(passwordId), eq("user-id"))).thenReturn(expectedResponse);

        // act
        ResponseEntity<PasswordResponseRecord> response = passwordController.getPassword(passwordId, mockToken);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be 200");
        assertEquals(expectedResponse, response.getBody(), "Response body should be equals to expectedResponse");
        verify(passwordService).getPassword(passwordId, "user-id");
    }

    // update password
    @Test
    void shouldSuccessfullyUpdatePassword() throws Exception {
        // arrange
        UUID passwordId = UUID.randomUUID();
        PasswordRequestRecord request = new PasswordRequestRecord("test-label", "test-password");
        PasswordResponseRecord expectedResponse = new PasswordResponseRecord(passwordId, "test-label", "test-password",
                null);

        when(passwordService.updatePassword(eq(passwordId), eq(request), eq("user-id"))).thenReturn(expectedResponse);

        // act
        ResponseEntity<PasswordResponseRecord> response = passwordController.updatePassword(passwordId, request,
                mockToken);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be 200");
        assertEquals(expectedResponse, response.getBody(), "Response body should be equals to expectedResponse");
        verify(passwordService).updatePassword(passwordId, request, "user-id");
    }

    // delete password
    @Test
    void shouldSuccessfullyDeletePassword() throws Exception {
        // arrange
        UUID passwordId = UUID.randomUUID();

        // act
        ResponseEntity<Void> response = passwordController.deletePassword(passwordId, mockToken);

        // assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Status code should be 204");
        verify(passwordService).deletePassword(passwordId, "user-id");
    }

}

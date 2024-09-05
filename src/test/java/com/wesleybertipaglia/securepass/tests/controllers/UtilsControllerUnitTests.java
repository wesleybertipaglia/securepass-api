package com.wesleybertipaglia.securepass.tests.controllers;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.wesleybertipaglia.securepass.controllers.UtilsController;
import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerRequestRecord;
import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerResponseRecord;
import com.wesleybertipaglia.securepass.records.generator.PasswordGeneratorResponseRecord;
import com.wesleybertipaglia.securepass.services.checker.PasswordCheckerService;
import com.wesleybertipaglia.securepass.services.generator.PasswordGeneratorService;

public class UtilsControllerUnitTests {

    @Mock
    private PasswordCheckerService passwordCheckerService;

    @Mock
    private PasswordGeneratorService passwordGeneratorService;

    @InjectMocks
    private UtilsController utilsController;

    private JwtAuthenticationToken mockToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // mock access token
        mockToken = mock(JwtAuthenticationToken.class);
        when(mockToken.getName()).thenReturn("user-id");
    }

    // checker
    @Test
    void shouldSuccessfullyCheckPassword() {
        // Arrange
        PasswordCheckerRequestRecord request = new PasswordCheckerRequestRecord("password123");
        PasswordCheckerResponseRecord expectedResponse = new PasswordCheckerResponseRecord("Strong", List.of());

        when(passwordCheckerService.checkPassword(request)).thenReturn(expectedResponse);

        // Act
        PasswordCheckerResponseRecord actualResponse = utilsController.checkPassword(request).getBody();

        // Assert
        assertEquals(expectedResponse, actualResponse);
    }

    // generator
    @Test
    void shouldSuccessfullyGeneratePassword() {
        // Arrange
        PasswordGeneratorResponseRecord expectedResponse = new PasswordGeneratorResponseRecord(
                "generatedPassword",
                new PasswordGeneratorResponseRecord.GenerationProperties(12, true, true, true, true));

        when(passwordGeneratorService.generatePassword(12, true, true, true, true)).thenReturn(expectedResponse);

        // Act
        PasswordGeneratorResponseRecord actualResponse = utilsController.generatePassword(12, true, true, true, true)
                .getBody();

        // Assert
        assertEquals(expectedResponse, actualResponse);
    }
}

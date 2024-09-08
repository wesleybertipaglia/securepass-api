package com.wesleybertipaglia.securepass.controllers;

import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerRequestRecord;
import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerResponseRecord;
import com.wesleybertipaglia.securepass.records.generator.PasswordGeneratorResponseRecord;
import com.wesleybertipaglia.securepass.services.checker.PasswordCheckerService;
import com.wesleybertipaglia.securepass.services.generator.PasswordGeneratorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtilsControllerTest {

    @InjectMocks
    private UtilsController utilsController;

    @Mock
    private PasswordCheckerService passwordCheckerService;

    @Mock
    private PasswordGeneratorService passwordGeneratorService;

    private static final String PASSWORD_VALUE = "P@ssw0rd123";
    private static final String ERROR_MESSAGE_BLANK_PASSWORD = "Password cannot be blank";
    private static final String ERROR_MESSAGE_LENGTH_ZERO = "Password length must be greater than zero";
    private static final String ERROR_MESSAGE_NO_CHARSET = "At least one character set must be selected";

    @Nested
    @DisplayName("Password Checker Tests")
    class PasswordCheckerTests {

        @Test
        @DisplayName("Should successfully check password strength")
        void shouldSuccessfullyCheckPassword() {
            // Arrange
            PasswordCheckerRequestRecord request = new PasswordCheckerRequestRecord(PASSWORD_VALUE);
            PasswordCheckerResponseRecord expectedResponse = new PasswordCheckerResponseRecord("Strong", List.of());
            when(passwordCheckerService.checkPassword(request)).thenReturn(expectedResponse);

            // Act
            PasswordCheckerResponseRecord actualResponse = utilsController.checkPassword(request).getBody();

            // Assert
            assertNotNull(actualResponse, "Response should not be null");
            assertEquals(expectedResponse, actualResponse, "Password strength response should match expected");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when password is blank")
        void shouldThrowExceptionWhenPasswordIsBlank() {
            // Arrange
            PasswordCheckerRequestRecord request = new PasswordCheckerRequestRecord("");
            when(passwordCheckerService.checkPassword(any())).thenCallRealMethod();

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> utilsController.checkPassword(request),
                    "Should throw IllegalArgumentException when password is blank");
            assertEquals(ERROR_MESSAGE_BLANK_PASSWORD, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Password Generator Tests")
    class PasswordGeneratorTests {

        @Test
        @DisplayName("Should successfully generate a strong password")
        void shouldSuccessfullyGeneratePassword() {
            // Arrange
            PasswordGeneratorResponseRecord.GenerationProperties generationProperties = new PasswordGeneratorResponseRecord.GenerationProperties(
                    12, true, true, true, true);
            PasswordGeneratorResponseRecord expectedResponse = new PasswordGeneratorResponseRecord(PASSWORD_VALUE, generationProperties);
            when(passwordGeneratorService.generatePassword(12, true, true, true, true)).thenReturn(expectedResponse);

            // Act
            PasswordGeneratorResponseRecord actualResponse = utilsController.generatePassword(12, true, true, true, true).getBody();

            // Assert
            assertNotNull(actualResponse, "Response should not be null");
            assertEquals(expectedResponse, actualResponse, "Generated password should match expected");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when password length is 0")
        void shouldThrowExceptionWhenLengthIs0() {
            // Arrange
            when(passwordGeneratorService.generatePassword(0, true, true, true, true)).thenCallRealMethod();

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> utilsController.generatePassword(0, true, true, true, true),
                    "Should throw IllegalArgumentException when password length is 0");
            assertEquals(ERROR_MESSAGE_LENGTH_ZERO, exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when no character sets are selected")
        void shouldThrowExceptionWhenAnyCharsetIsSelected() {
            // Arrange
            when(passwordGeneratorService.generatePassword(12, false, false, false, false)).thenCallRealMethod();

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> utilsController.generatePassword(12, false, false, false, false),
                    "Should throw IllegalArgumentException when no character sets are selected");
            assertEquals(ERROR_MESSAGE_NO_CHARSET, exception.getMessage());
        }
    }

}
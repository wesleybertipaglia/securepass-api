package com.wesleybertipaglia.securepass.services.checker;

import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerRequestRecord;
import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerResponseRecord;
import com.wesleybertipaglia.securepass.services.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PasswordCheckerServiceTest {

    @InjectMocks
    private PasswordCheckerService passwordCheckerService;

    @BeforeEach
    void setup() {
        injectValidationStrategies();
    }

    private void injectValidationStrategies() {
        List<ValidationStrategyInterface> strategies = List.of(
                new LengthValidation(),
                new LowercaseValidation(),
                new UppercaseValidation(),
                new NumberValidation(),
                new SpecialCharacterValidation());

        try {
            Field field = PasswordCheckerService.class.getDeclaredField("validationStrategies");
            field.setAccessible(true);
            field.set(passwordCheckerService, strategies);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject validation strategies", e);
        }
    }

    @Test
    @DisplayName("Should return weak strength and correct suggestions.")
    void shouldReturnWeakStrength() {
        // arrange
        PasswordCheckerRequestRecord passwordCheckerRequestRecord = new PasswordCheckerRequestRecord("pass");
        String expectedStrength = "Weak";

        // act
        PasswordCheckerResponseRecord passwordCheckerResponseRecord = passwordCheckerService.checkPassword(passwordCheckerRequestRecord);
        String actualStrength = passwordCheckerResponseRecord.strength();
        List<String> actualSuggestions = passwordCheckerResponseRecord.suggestions();

        // assert
        assertEquals(expectedStrength, actualStrength, "Expected password strength to be Weak");
        assertEquals(4, actualSuggestions.size(), "Expected 4 suggestions for a weak password");
    }

    @Test
    @DisplayName("Should return medium strength and correct suggestions.")
    void shouldReturnMediumStrength() {
        // arrange
        PasswordCheckerRequestRecord passwordCheckerRequestRecord = new PasswordCheckerRequestRecord("Password");
        String expectedStrength = "Medium";

        // act
        PasswordCheckerResponseRecord passwordCheckerResponseRecord = passwordCheckerService.checkPassword(passwordCheckerRequestRecord);
        String actualStrength = passwordCheckerResponseRecord.strength();
        List<String> actualSuggestions = passwordCheckerResponseRecord.suggestions();

        // assert
        assertEquals(expectedStrength, actualStrength, "Expected password strength to be Medium");
        assertEquals(2, actualSuggestions.size(), "Expected 2 suggestions for a medium password");
    }

    @Test
    @DisplayName("Should return strong strength and correct suggestions.")
    void shouldReturnStrongStrength() {
        // arrange
        PasswordCheckerRequestRecord passwordCheckerRequestRecord = new PasswordCheckerRequestRecord("Password1!");
        String expectedStrength = "Strong";

        // act
        PasswordCheckerResponseRecord passwordCheckerResponseRecord = passwordCheckerService.checkPassword(passwordCheckerRequestRecord);
        String actualStrength = passwordCheckerResponseRecord.strength();
        List<String> actualSuggestions = passwordCheckerResponseRecord.suggestions();

        // assert
        assertEquals(expectedStrength, actualStrength, "Expected password strength to be Strong");
        assertEquals(0, actualSuggestions.size(), "Expected 2 suggestions for a strong password");
    }

    @Test
    @DisplayName("Should throw an exception when password is bank.")
    void shouldThrowAnExceptionWhenPasswordIsBlank() {
        // arrange
        PasswordCheckerRequestRecord passwordCheckerRequestRecord = new PasswordCheckerRequestRecord("");

        // act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> passwordCheckerService.checkPassword(passwordCheckerRequestRecord));

        // assert
        assertEquals(exception.getMessage(), "Password cannot be blank");
    }
}
package com.wesleybertipaglia.securepass.tests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerRequestRecord;
import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerResponseRecord;
import com.wesleybertipaglia.securepass.services.checker.PasswordCheckerService;
import com.wesleybertipaglia.securepass.services.validation.*;

import java.lang.reflect.Field;
import java.util.List;

public class PasswordCheckerServiceUnitTests {

    @InjectMocks
    private PasswordCheckerService passwordCheckerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // inject validation strategies into the service
        injectValidationStrategies();
    }

    // inject validation strategies into PasswordCheckerService
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
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to inject validation strategies", e);
        }
    }

    @Test
    void shouldReturnWeakPassword() {
        // arrange
        PasswordCheckerRequestRecord request = new PasswordCheckerRequestRecord("short");

        // act
        PasswordCheckerResponseRecord response = passwordCheckerService.checkPassword(request);

        // assert
        assertEquals("Weak", response.strength(), "Expected password strength to be Weak");
        assertEquals(4, response.suggestions().size(), "Expected 4 suggestions for a weak password");
    }

    @Test
    void shouldReturnMediumPassword() {
        // arrange
        PasswordCheckerRequestRecord request = new PasswordCheckerRequestRecord("Strong!Password");

        // act
        PasswordCheckerResponseRecord response = passwordCheckerService.checkPassword(request);

        // assert
        assertEquals("Medium", response.strength(), "Expected password strength to be Medium");
        assertEquals(1, response.suggestions().size(), "Expected 1 suggestion for a medium password");
    }

    @Test
    void shouldReturnStrongPassword() {
        // arrange
        PasswordCheckerRequestRecord request = new PasswordCheckerRequestRecord("Str0ng!Password");

        // act
        PasswordCheckerResponseRecord response = passwordCheckerService.checkPassword(request);

        // assert
        assertEquals("Strong", response.strength(), "Expected password strength to be Strong");
        assertEquals(0, response.suggestions().size(), "Expected no suggestions for a strong password");
    }
}

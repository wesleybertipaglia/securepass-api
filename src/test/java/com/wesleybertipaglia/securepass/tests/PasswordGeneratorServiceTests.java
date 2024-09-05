package com.wesleybertipaglia.securepass.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wesleybertipaglia.securepass.records.generator.PasswordGeneratorResponseRecord;
import com.wesleybertipaglia.securepass.services.generator.PasswordGeneratorService;

public class PasswordGeneratorServiceTests {
    private PasswordGeneratorService passwordGeneratorService;

    @BeforeEach
    void setUp() {
        passwordGeneratorService = new PasswordGeneratorService();
    }

    @Test
    void shouldThrowExceptionWhenLengthIsZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> passwordGeneratorService.generatePassword(0, true, true, true, true));
        assertEquals("Password length must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNoCharacterSetIsSelected() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> passwordGeneratorService.generatePassword(10, false, false, false, false));
        assertEquals("At least one character set must be selected", exception.getMessage());
    }

    @Test
    void shouldGeneratePasswordWithUppercaseOnly() {
        PasswordGeneratorResponseRecord response = passwordGeneratorService.generatePassword(10, true, false, false,
                false);
        String password = response.password();

        assertEquals(10, password.length());
        assertTrue(password.chars().allMatch(c -> Character.isUpperCase(c)));
    }

    @Test
    void shouldGeneratePasswordWithLowercaseOnly() {
        PasswordGeneratorResponseRecord response = passwordGeneratorService.generatePassword(10, false, true, false,
                false);
        String password = response.password();

        assertEquals(10, password.length());
        assertTrue(password.chars().allMatch(c -> Character.isLowerCase(c)));
    }

    @Test
    void shouldGeneratePasswordWithNumbersOnly() {
        PasswordGeneratorResponseRecord response = passwordGeneratorService.generatePassword(10, false, false, true,
                false);
        String password = response.password();

        assertEquals(10, password.length());
        assertTrue(password.chars().allMatch(c -> Character.isDigit(c)));
    }

    @Test
    void shouldGeneratePasswordWithSpecialCharactersOnly() {
        PasswordGeneratorResponseRecord response = passwordGeneratorService.generatePassword(10, false, false, false,
                true);
        String password = response.password();

        assertEquals(10, password.length());
        assertTrue(password.chars().allMatch(c -> "!@#$%^&*()-_+=<>?".indexOf(c) >= 0));
    }

    @Test
    void shouldGeneratePasswordWithAllCharacterSets() {
        PasswordGeneratorResponseRecord response = passwordGeneratorService.generatePassword(20, true, true, true,
                true);
        String password = response.password();

        assertEquals(20, password.length());
        assertTrue(password.chars().anyMatch(c -> Character.isUpperCase(c)));
        assertTrue(password.chars().anyMatch(c -> Character.isLowerCase(c)));
        assertTrue(password.chars().anyMatch(c -> Character.isDigit(c)));
        assertTrue(password.chars().anyMatch(c -> "!@#$%^&*()-_+=<>?".indexOf(c) >= 0));
    }

}
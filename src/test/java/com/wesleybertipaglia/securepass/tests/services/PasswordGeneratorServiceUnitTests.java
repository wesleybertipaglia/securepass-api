package com.wesleybertipaglia.securepass.tests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.wesleybertipaglia.securepass.records.generator.PasswordGeneratorResponseRecord;
import com.wesleybertipaglia.securepass.services.generator.PasswordGeneratorService;

public class PasswordGeneratorServiceUnitTests {

    @InjectMocks
    private PasswordGeneratorService passwordGeneratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldThrowExceptionWhenLengthIsZero() {
        // arrange
        int length = 0;
        boolean useUppercase = true;
        boolean useLowercase = true;
        boolean useNumbers = true;
        boolean useSpecialChars = true;

        // act & assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> passwordGeneratorService.generatePassword(length, useUppercase, useLowercase, useNumbers,
                        useSpecialChars));
        assertEquals("Password length must be greater than zero", exception.getMessage(),
                "Expected exception message for zero length password");
    }

    @Test
    void shouldThrowExceptionWhenNoCharacterSetIsSelected() {
        // arrange
        int length = 10;
        boolean useUppercase = false;
        boolean useLowercase = false;
        boolean useNumbers = false;
        boolean useSpecialChars = false;

        // act & assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> passwordGeneratorService.generatePassword(length, useUppercase, useLowercase, useNumbers,
                        useSpecialChars));
        assertEquals("At least one character set must be selected", exception.getMessage(),
                "Expected exception message for no character set selected");
    }

    @Test
    void shouldGeneratePasswordWithUppercaseOnly() {
        // arrange
        int length = 10;
        boolean useUppercase = true;
        boolean useLowercase = false;
        boolean useNumbers = false;
        boolean useSpecialChars = false;

        // act
        PasswordGeneratorResponseRecord response = passwordGeneratorService.generatePassword(length, useUppercase,
                useLowercase, useNumbers, useSpecialChars);
        String password = response.password();

        // assert
        assertEquals(length, password.length(), "Password length should match the requested length");
        assertTrue(password.chars().allMatch(Character::isUpperCase), "Password should contain only uppercase letters");
    }

    @Test
    void shouldGeneratePasswordWithLowercaseOnly() {
        // arrange
        int length = 10;
        boolean useUppercase = false;
        boolean useLowercase = true;
        boolean useNumbers = false;
        boolean useSpecialChars = false;

        // act
        PasswordGeneratorResponseRecord response = passwordGeneratorService.generatePassword(length, useUppercase,
                useLowercase, useNumbers, useSpecialChars);
        String password = response.password();

        // assert
        assertEquals(length, password.length(), "Password length should match the requested length");
        assertTrue(password.chars().allMatch(Character::isLowerCase), "Password should contain only lowercase letters");
    }

    @Test
    void shouldGeneratePasswordWithNumbersOnly() {
        // arrange
        int length = 10;
        boolean useUppercase = false;
        boolean useLowercase = false;
        boolean useNumbers = true;
        boolean useSpecialChars = false;

        // act
        PasswordGeneratorResponseRecord response = passwordGeneratorService.generatePassword(length, useUppercase,
                useLowercase, useNumbers, useSpecialChars);
        String password = response.password();

        // assert
        assertEquals(length, password.length(), "Password length should match the requested length");
        assertTrue(password.chars().allMatch(Character::isDigit), "Password should contain only digits");
    }

    @Test
    void shouldGeneratePasswordWithSpecialCharactersOnly() {
        // arrange
        int length = 10;
        boolean useUppercase = false;
        boolean useLowercase = false;
        boolean useNumbers = false;
        boolean useSpecialChars = true;

        // act
        PasswordGeneratorResponseRecord response = passwordGeneratorService.generatePassword(length, useUppercase,
                useLowercase, useNumbers, useSpecialChars);
        String password = response.password();

        // assert
        assertEquals(length, password.length(), "Password length should match the requested length");
        assertTrue(password.chars().allMatch(c -> "!@#$%^&*()-_+=<>?".indexOf(c) >= 0),
                "Password should contain only special characters");
    }

    @Test
    void shouldGeneratePasswordWithAllCharacterSets() {
        // arrange
        int length = 20;
        boolean useUppercase = true;
        boolean useLowercase = true;
        boolean useNumbers = true;
        boolean useSpecialChars = true;

        // act
        PasswordGeneratorResponseRecord response = passwordGeneratorService.generatePassword(length, useUppercase,
                useLowercase, useNumbers, useSpecialChars);
        String password = response.password();

        // assert
        assertEquals(length, password.length(), "Password length should match the requested length");
        assertTrue(password.chars().anyMatch(Character::isUpperCase), "Password should contain uppercase letters");
        assertTrue(password.chars().anyMatch(Character::isLowerCase), "Password should contain lowercase letters");
        assertTrue(password.chars().anyMatch(Character::isDigit), "Password should contain digits");
        assertTrue(password.chars().anyMatch(c -> "!@#$%^&*()-_+=<>?".indexOf(c) >= 0),
                "Password should contain special characters");
    }
}

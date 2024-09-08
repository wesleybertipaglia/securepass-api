package com.wesleybertipaglia.securepass.services.generator;

import com.wesleybertipaglia.securepass.records.generator.PasswordGeneratorResponseRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PasswordGeneratorServiceTest {

    @InjectMocks
    PasswordGeneratorService passwordGeneratorService;

    private static final int PASSWORD_LENGTH = 12;

    private String generatePassword(boolean includeUppercase, boolean includeLowercase, boolean includeNumbers, boolean includeSpecial) {
        PasswordGeneratorResponseRecord passwordResponseRecord = passwordGeneratorService.generatePassword(PASSWORD_LENGTH, includeUppercase, includeLowercase, includeNumbers, includeSpecial);
        return passwordResponseRecord.password();
    }

    @Test
    @DisplayName("Should generate a random password with upper cases only.")
    void shouldGeneratePasswordWithUpperCaseOnly() {
        // arrange
        boolean includeUppercase = true;
        boolean includeLowercase = false;
        boolean includeNumbers = false;
        boolean includeSpecial = false;

        // act
        String password = generatePassword(includeUppercase, includeLowercase, includeNumbers, includeSpecial);

        // assert
        assertNotNull(password, "Password should not be null.");
        assertEquals(password.length(), PASSWORD_LENGTH, "Password length should match the requested length.");
        assertTrue(password.chars().allMatch(Character::isUpperCase), "Password should contain only upper case characters.");
    }

    @Test
    @DisplayName("Should generate a random password with lower cases only.")
    void shouldGeneratePasswordWithLowerCaseOnly() {
        // arrange
        boolean includeUppercase = false;
        boolean includeLowercase = true;
        boolean includeNumbers = false;
        boolean includeSpecial = false;

        // act
        String password = generatePassword(includeUppercase, includeLowercase, includeNumbers, includeSpecial);

        // assert
        assertNotNull(password, "Password should not be null.");
        assertEquals(password.length(), PASSWORD_LENGTH, "Password length should match the requested length.");
        assertTrue(password.chars().allMatch(Character::isLowerCase), "Password should contain only lower case characters.");
    }

    @Test
    @DisplayName("Should generate a random password with numbers only. special characters.")
    void shouldGeneratePasswordWithNumbersOnly() {
        // arrange
        boolean includeUppercase = false;
        boolean includeLowercase = false;
        boolean includeNumbers = true;
        boolean includeSpecial = false;

        // act
        String password = generatePassword(includeUppercase, includeLowercase, includeNumbers, includeSpecial);

        // assert
        assertNotNull(password, "Password should not be null.");
        assertEquals(password.length(), PASSWORD_LENGTH, "Password length should match the requested length.");
        assertTrue(password.chars().allMatch(Character::isDigit), "Password should contain only numbers.");
    }

    @Test
    @DisplayName("Should generate a random password with special characters only.")
    void shouldGeneratePasswordWithSpecialCharactersOnly() {
        // arrange
        boolean includeUppercase = false;
        boolean includeLowercase = false;
        boolean includeNumbers = false;
        boolean includeSpecial = true;

        // act
        String password = generatePassword(includeUppercase, includeLowercase, includeNumbers, includeSpecial);

        // assert
        assertNotNull(password, "Password should not be null.");
        assertEquals(password.length(), PASSWORD_LENGTH, "Password length should match the requested length.");
        assertTrue(password.chars().allMatch(c -> "!@#$%^&*()-_+=<>?".indexOf(c) >= 0), "Password should contain only special characters.");
    }

    @Test
    @DisplayName("Should throw an exception when length is 0.")
    void shouldThrowExceptionWhenLengthIsZero() {
        // arrange
        int passwordLength = 0;
        boolean includeUppercase = false;
        boolean includeLowercase = false;
        boolean includeNumbers = false;
        boolean includeSpecial = true;

        // act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> passwordGeneratorService.generatePassword(passwordLength, includeUppercase, includeLowercase,  includeNumbers, includeSpecial));

        // assert
        assertEquals("Password length must be greater than zero", exception.getMessage(),
                "Expected exception message for zero length password");
    }

    @Test
    @DisplayName("Should throw an exception when no charset is selected.")
    void shouldThrowExceptionWhenNoCharacterSetIsSelected() {
        // arrange
        boolean includeUppercase = false;
        boolean includeLowercase = false;
        boolean includeNumbers = false;
        boolean includeSpecial = false;

        // act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> passwordGeneratorService.generatePassword(PASSWORD_LENGTH, includeUppercase, includeLowercase,  includeNumbers, includeSpecial));

        // assert
        assertEquals("At least one character set must be selected", exception.getMessage(),
                "Expected exception message for no character set selected");
    }
}
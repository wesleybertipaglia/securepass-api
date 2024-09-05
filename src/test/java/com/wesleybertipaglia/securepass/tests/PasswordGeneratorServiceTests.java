package com.wesleybertipaglia.securepass.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wesleybertipaglia.securepass.records.generator.PasswordGeneratorResponseRecord;
import com.wesleybertipaglia.securepass.services.generator.PasswordGeneratorService;

public class PasswordGeneratorServiceTests {
    private PasswordGeneratorService passwordGeneratorService;

    @BeforeEach
    public void setup() {
        passwordGeneratorService = new PasswordGeneratorService();
    }

    @Test
    public void testGeneratePasswordSuccess() {
        PasswordGeneratorResponseRecord passwordResponseRecord = passwordGeneratorService.generatePassword(10, true,
                true,
                true, true);

        assertEquals(passwordResponseRecord.properties().passwordLength(), 10);
        assertEquals(passwordResponseRecord.properties().includeUppercase(), true);
        assertEquals(passwordResponseRecord.properties().includeLowercase(), true);
        assertEquals(passwordResponseRecord.properties().includeNumbers(), true);
        assertEquals(passwordResponseRecord.properties().includeSpecial(), true);

        String password = passwordResponseRecord.password();

        assertEquals(password.length(), 10);
        assertEquals(password.chars().anyMatch(Character::isUpperCase), true);
        assertEquals(password.chars().anyMatch(Character::isLowerCase), true);
        assertEquals(password.chars().anyMatch(Character::isDigit), true);
        assertEquals(password.chars().anyMatch(c -> "!@#$%^&*()-_+=<>?".indexOf(c) >= 0), true);
    }

    @Test
    public void testGeneratePasswordFailure() {
        try {
            passwordGeneratorService.generatePassword(0, true, true, true, true);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Password length must be greater than zero");
        }

        try {
            passwordGeneratorService.generatePassword(10, false, false, false, false);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "At least one character set must be selected");
        }
    }
}
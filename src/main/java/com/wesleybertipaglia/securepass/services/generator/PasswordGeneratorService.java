package com.wesleybertipaglia.securepass.services.generator;

import org.springframework.stereotype.Service;

import com.wesleybertipaglia.securepass.records.generator.PasswordGeneratorResponseRecord;

import java.security.SecureRandom;

@Service
public class PasswordGeneratorService implements PasswordGeneratorServiceInterface {

    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?";

    private final SecureRandom random = new SecureRandom();

    public PasswordGeneratorResponseRecord generatePassword(int length, boolean includeUppercase,
            boolean includeLowercase, boolean includeNumbers, boolean includeSpecial) {
        validateLength(length);
        String characterSet = buildCharacterSet(includeUppercase, includeLowercase, includeNumbers, includeSpecial);
        validateCharacterSet(characterSet);

        String password = generateRandomPassword(length, characterSet);
        PasswordGeneratorResponseRecord.GenerationProperties properties = new PasswordGeneratorResponseRecord.GenerationProperties(
                length, includeUppercase, includeLowercase, includeNumbers, includeSpecial);
        return new PasswordGeneratorResponseRecord(password, properties);
    }

    private void validateLength(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be greater than zero");
        }
    }

    private String buildCharacterSet(boolean includeUppercase, boolean includeLowercase, boolean includeNumbers,
            boolean includeSpecial) {
        StringBuilder characterSet = new StringBuilder();
        if (includeUppercase)
            characterSet.append(UPPERCASE_LETTERS);
        if (includeLowercase)
            characterSet.append(LOWERCASE_LETTERS);
        if (includeNumbers)
            characterSet.append(NUMBERS);
        if (includeSpecial)
            characterSet.append(SPECIAL_CHARACTERS);
        return characterSet.toString();
    }

    private void validateCharacterSet(String characterSet) {
        if (characterSet.isEmpty()) {
            throw new IllegalArgumentException("At least one character set must be selected");
        }
    }

    private String generateRandomPassword(int length, String characterSet) {
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(characterSet.charAt(random.nextInt(characterSet.length())));
        }
        return password.toString();
    }
}

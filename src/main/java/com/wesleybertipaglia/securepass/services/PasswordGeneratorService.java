package com.wesleybertipaglia.securepass.services;

import org.springframework.stereotype.Service;

import com.wesleybertipaglia.securepass.records.generator.PasswordGeneratorResponseRecord;

import java.security.SecureRandom;

@Service
public class PasswordGeneratorService {

    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?";

    private SecureRandom random = new SecureRandom();

    public PasswordGeneratorResponseRecord generatePassword(int length, boolean includeUppercase,
            boolean includeLowercase,
            boolean includeNumbers, boolean includeSpecial) {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be greater than zero");
        }

        StringBuilder characterSet = new StringBuilder();
        if (includeUppercase) {
            characterSet.append(UPPERCASE_LETTERS);
        }
        if (includeLowercase) {
            characterSet.append(LOWERCASE_LETTERS);
        }
        if (includeNumbers) {
            characterSet.append(NUMBERS);
        }
        if (includeSpecial) {
            characterSet.append(SPECIAL_CHARACTERS);
        }

        if (characterSet.length() == 0) {
            throw new IllegalArgumentException("At least one character set must be selected");
        }

        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(characterSet.charAt(random.nextInt(characterSet.length())));
        }

        PasswordGeneratorResponseRecord.GenerationProperties properties = new PasswordGeneratorResponseRecord.GenerationProperties(
                length, includeUppercase, includeLowercase, includeNumbers, includeSpecial);
        return new PasswordGeneratorResponseRecord(password.toString(), properties);
    }
}

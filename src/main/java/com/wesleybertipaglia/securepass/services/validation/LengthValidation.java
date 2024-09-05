package com.wesleybertipaglia.securepass.services.validation;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class LengthValidation implements ValidationStrategy {
    private static final int MIN_LENGTH = 8;

    @Override
    public void validate(String password, List<String> suggestions) {
        if (password.length() < MIN_LENGTH) {
            suggestions.add("Password must be at least " + MIN_LENGTH + " characters long");
        }
    }
}
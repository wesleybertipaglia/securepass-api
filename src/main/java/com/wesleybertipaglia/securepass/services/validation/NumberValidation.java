package com.wesleybertipaglia.securepass.services.validation;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class NumberValidation implements ValidationStrategy {

    @Override
    public void validate(String password, List<String> suggestions) {
        if (!password.matches(".*[0-9].*")) {
            suggestions.add("Password must contain at least one number");
        }
    }
}
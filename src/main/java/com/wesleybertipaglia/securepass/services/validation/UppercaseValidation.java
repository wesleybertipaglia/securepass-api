package com.wesleybertipaglia.securepass.services.validation;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class UppercaseValidation implements ValidationStrategyInterface {

    @Override
    public void validate(String password, List<String> suggestions) {
        if (!password.matches(".*[A-Z].*")) {
            suggestions.add("Password must contain at least one uppercase letter");
        }
    }
}
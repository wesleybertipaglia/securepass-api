package com.wesleybertipaglia.securepass.services.validation;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class SpecialCharacterValidation implements ValidationStrategyInterface {

    @Override
    public void validate(String password, List<String> suggestions) {
        if (!password.matches(".*[\\W].*")) {
            suggestions.add("Password must contain at least one special character (e.g., !@#$%^&*)");
        }
    }
}
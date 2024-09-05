package com.wesleybertipaglia.securepass.services.validation;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class LowercaseValidation implements ValidationStrategyInterface {

    @Override
    public void validate(String password, List<String> suggestions) {
        if (!password.matches(".*[a-z].*")) {
            suggestions.add("Password must contain at least one lowercase letter");
        }
    }
}

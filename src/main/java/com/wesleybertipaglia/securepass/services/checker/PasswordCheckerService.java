package com.wesleybertipaglia.securepass.services.checker;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.wesleybertipaglia.securepass.services.validation.ValidationStrategyInterface;
import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerRequestRecord;
import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerResponseRecord;

@Service
public class PasswordCheckerService implements PasswordCheckerServiceInterface {

    @Autowired
    private List<ValidationStrategyInterface> validationStrategies;

    public PasswordCheckerResponseRecord checkPassword(PasswordCheckerRequestRecord passwordCheckerRequestRecord) {
        String password = passwordCheckerRequestRecord.password();

        if (password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }

        List<String> suggestions = determineSuggestions(password);
        String strength = determinePasswordStrength(password, suggestions);
        return new PasswordCheckerResponseRecord(strength, suggestions);
    }

    private List<String> determineSuggestions(String password) {
        List<String> suggestions = new ArrayList<>();
        for (ValidationStrategyInterface strategy : validationStrategies) {
            strategy.validate(password, suggestions);
        }
        return  suggestions;
    }

    private String determinePasswordStrength(String password, List<String> suggestions) {
        if (suggestions.isEmpty()) {
            return "Strong";
        } else if (suggestions.size() <= 2 && password.length() >= 8) {
            return "Medium";
        }
        return "Weak";
    }

}

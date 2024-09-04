package com.wesleybertipaglia.securepass.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerRequestRecord;
import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerResponseRecord;

@Service
public class PasswordCheckerService {

    public PasswordCheckerResponseRecord checkPassword(PasswordCheckerRequestRecord passwordRequestDTO) {
        String password = passwordRequestDTO.password();
        List<String> suggestions = new ArrayList<>();

        checkPasswordLength(password, suggestions);
        checkPasswordUppercase(password, suggestions);
        checkPasswordLowercase(password, suggestions);
        checkPasswordNumber(password, suggestions);
        checkPasswordSpecialCharacter(password, suggestions);

        String strength = checkPasswordStrength(password, suggestions);
        return new PasswordCheckerResponseRecord(strength, suggestions);
    }

    private String checkPasswordStrength(String password, List<String> suggestions) {
        if (suggestions.isEmpty()) {
            return "Strong";
        }

        if (suggestions.size() <= 2 && password.length() >= 8) {
            return "Medium";
        }

        return "Weak";
    }

    private void checkPasswordLength(String password, List<String> suggestions) {
        if (password.length() < 8) {
            suggestions.add("Password must be at least 8 characters long");
        }
    }

    private void checkPasswordUppercase(String password, List<String> suggestions) {
        if (!password.matches(".*[A-Z].*")) {
            suggestions.add("Password must contain at least one uppercase letter");
        }
    }

    private void checkPasswordLowercase(String password, List<String> suggestions) {
        if (!password.matches(".*[a-z].*")) {
            suggestions.add("Password must contain at least one lowercase letter");
        }
    }

    private void checkPasswordNumber(String password, List<String> suggestions) {
        if (!password.matches(".*[0-9].*")) {
            suggestions.add("Password must contain at least one number");
        }
    }

    private void checkPasswordSpecialCharacter(String password, List<String> suggestions) {
        if (!password.matches(".*[\\W].*")) {
            suggestions.add("Password must contain at least one special character (e.g. !@#$%^&*)");
        }
    }

}

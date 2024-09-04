package com.wesleybertipaglia.securepass.checker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerRequestRecord;
import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerResponseRecord;
import com.wesleybertipaglia.securepass.services.PasswordCheckerService;

public class PasswordCheckerServiceTests {
    private PasswordCheckerService passwordCheckerService;

    @BeforeEach
    public void setUp() {
        passwordCheckerService = new PasswordCheckerService();
    }

    @Test
    public void testCheckPasswordStrong() {
        PasswordCheckerRequestRecord passwordRequestRecord = new PasswordCheckerRequestRecord("Password123!");
        PasswordCheckerResponseRecord response = passwordCheckerService.checkPassword(passwordRequestRecord);

        assertEquals("Strong", response.strength());
        assertEquals(0, response.suggestions().size());
    }

    @Test
    public void testCheckPasswordMedium() {
        PasswordCheckerRequestRecord passwordRequestRecord = new PasswordCheckerRequestRecord("Password123");
        PasswordCheckerResponseRecord response = passwordCheckerService.checkPassword(passwordRequestRecord);

        assertEquals("Medium", response.strength());
        assertEquals(1, response.suggestions().size());
    }

    @Test
    public void testCheckPasswordWeak() {
        PasswordCheckerRequestRecord passwordRequestRecord = new PasswordCheckerRequestRecord("passwor");
        PasswordCheckerResponseRecord response = passwordCheckerService.checkPassword(passwordRequestRecord);

        assertEquals("Weak", response.strength());
        assertEquals(4, response.suggestions().size());
        assertEquals(response.suggestions().get(0), "Password must be at least 8 characters long");
        assertEquals(response.suggestions().get(1), "Password must contain at least one uppercase letter");
        assertEquals(response.suggestions().get(2), "Password must contain at least one number");
        assertEquals(response.suggestions().get(3),
                "Password must contain at least one special character (e.g. !@#$%^&*)");
    }
}

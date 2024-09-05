package com.wesleybertipaglia.securepass.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerRequestRecord;
import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerResponseRecord;
import com.wesleybertipaglia.securepass.services.checker.PasswordCheckerService;
import com.wesleybertipaglia.securepass.services.validation.*;

public class PasswordCheckerServiceTests {

    @InjectMocks
    private PasswordCheckerService passwordCheckerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        List<ValidationStrategyInterface> strategies = new ArrayList<>();
        strategies.add(new LengthValidation());
        strategies.add(new LowercaseValidation());
        strategies.add(new UppercaseValidation());
        strategies.add(new NumberValidation());
        strategies.add(new SpecialCharacterValidation());

        injectValidationStrategies(passwordCheckerService, strategies);
    }

    private void injectValidationStrategies(PasswordCheckerService service,
            List<ValidationStrategyInterface> strategies) {
        try {
            var field = PasswordCheckerService.class.getDeclaredField("validationStrategies");
            field.setAccessible(true);
            field.set(service, strategies);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Falha ao injetar validationStrategies", e);
        }
    }

    @Test
    void shouldReturnWeakPassword() {
        PasswordCheckerRequestRecord request = new PasswordCheckerRequestRecord("short");
        PasswordCheckerResponseRecord response = passwordCheckerService.checkPassword(request);
        assertEquals("Weak", response.strength());
        assertEquals(4, response.suggestions().size());
    }

    @Test
    void shouldReturnMediumPassword() {
        PasswordCheckerRequestRecord request = new PasswordCheckerRequestRecord("Strong!Password");
        PasswordCheckerResponseRecord response = passwordCheckerService.checkPassword(request);
        assertEquals("Medium", response.strength());
        assertEquals(1, response.suggestions().size());
    }

    @Test
    void shouldReturnStrongPassword() {
        PasswordCheckerRequestRecord request = new PasswordCheckerRequestRecord("Str0ng!Password");
        PasswordCheckerResponseRecord response = passwordCheckerService.checkPassword(request);
        assertEquals("Strong", response.strength());
        assertEquals(0, response.suggestions().size());
    }

}

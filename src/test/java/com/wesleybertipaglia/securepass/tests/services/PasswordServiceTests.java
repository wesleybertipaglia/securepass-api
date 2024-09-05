package com.wesleybertipaglia.securepass.tests.services;

import java.util.UUID;
import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.wesleybertipaglia.securepass.entities.Password;
import com.wesleybertipaglia.securepass.entities.User;
import com.wesleybertipaglia.securepass.mappers.PasswordMapper;
import com.wesleybertipaglia.securepass.records.password.PasswordRequestRecord;
import com.wesleybertipaglia.securepass.records.password.PasswordResponseRecord;
import com.wesleybertipaglia.securepass.repositories.PasswordRepository;
import com.wesleybertipaglia.securepass.repositories.UserRepository;
import com.wesleybertipaglia.securepass.services.password.PasswordService;

import jakarta.persistence.EntityNotFoundException;

public class PasswordServiceTests {

    @InjectMocks
    private PasswordService passwordService;

    @Mock
    private PasswordRepository passwordRepository;

    @Mock
    private UserRepository userRepository;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        user = new User("Test User", "test@example.com", "encodedPassword");
        user.setId(userId);
    }

    @Test
    void shouldCreatePasswordSuccessfully() {
        PasswordRequestRecord request = new PasswordRequestRecord("Test Label", "Test Password");
        Password password = new Password("Test Label", "Test Password", user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        try (MockedStatic<PasswordMapper> mockedMapper = mockStatic(PasswordMapper.class)) {
            mockedMapper.when(() -> PasswordMapper.requestRecordToEntity(request, user)).thenReturn(password);
            mockedMapper.when(() -> PasswordMapper.entityToResponseRecord(password))
                    .thenReturn(
                            new PasswordResponseRecord(password.getId(), password.getLabel(), password.getPassword(),
                                    null));

            when(passwordRepository.save(any(Password.class))).thenReturn(password);
            PasswordResponseRecord response = passwordService.createPassword(request, userId.toString());
            assertNotNull(response);
            assertEquals("Test Label", response.label());
            assertEquals("Test Password", response.password());
        }
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnCreate() {
        PasswordRequestRecord request = new PasswordRequestRecord("Test Label", "Test Password");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> passwordService.createPassword(request, userId.toString()));
    }

    @Test
    void shouldListPasswordsSuccessfully() {
        Password password = new Password("Test Label", "Test Password", user);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Password> passwordPage = new PageImpl<>(List.of(password));
        when(passwordRepository.findAllByOwnerId(userId, pageable)).thenReturn(passwordPage);

        try (MockedStatic<PasswordMapper> mockedMapper = mockStatic(PasswordMapper.class)) {
            mockedMapper.when(() -> PasswordMapper.entityToResponseRecord(password))
                    .thenReturn(new PasswordResponseRecord(password.getId(), password.getLabel(),
                            password.getPassword(), null));
            Page<PasswordResponseRecord> response = passwordService.listPasswords(0, 10, userId.toString());

            assertNotNull(response);
            assertEquals(1, response.getTotalElements());
            assertEquals("Test Label", response.getContent().get(0).label());
        }
    }

    @Test
    void shouldGetPasswordSuccessfully() {
        UUID passwordId = UUID.randomUUID();
        Password password = new Password("Test Label", "Test Password", user);
        when(passwordRepository.findByIdAndOwnerId(passwordId, userId)).thenReturn(Optional.of(password));

        try (MockedStatic<PasswordMapper> mockedMapper = mockStatic(PasswordMapper.class)) {
            mockedMapper.when(() -> PasswordMapper.entityToResponseRecord(password))
                    .thenReturn(new PasswordResponseRecord(password.getId(), password.getLabel(),
                            password.getPassword(), null));
            PasswordResponseRecord response = passwordService.getPassword(passwordId, userId.toString());
            assertNotNull(response);
            assertEquals("Test Label", response.label());
        }
    }

    @Test
    void shouldThrowExceptionWhenPasswordNotFound() {
        UUID passwordId = UUID.randomUUID();
        when(passwordRepository.findByIdAndOwnerId(passwordId, userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> passwordService.getPassword(passwordId, userId.toString()));
    }

    @Test
    void shouldUpdatePasswordSuccessfully() {
        UUID passwordId = UUID.randomUUID();
        PasswordRequestRecord request = new PasswordRequestRecord("Updated Label", "Updated Password");
        Password password = new Password("Test Label", "Test Password", user);
        password.setId(passwordId);

        when(passwordRepository.findById(passwordId)).thenReturn(Optional.of(password));
        when(passwordRepository.save(any(Password.class))).thenReturn(password);

        try (MockedStatic<PasswordMapper> mockedMapper = mockStatic(PasswordMapper.class)) {
            mockedMapper.when(() -> PasswordMapper.entityToResponseRecord(password))
                    .thenReturn(
                            new PasswordResponseRecord(password.getId(), "Updated Label", "Updated Password", null));

            PasswordResponseRecord response = passwordService.updatePassword(passwordId, request, userId.toString());
            assertNotNull(response);
            assertEquals("Updated Label", response.label());
            assertEquals("Updated Password", response.password());
        }
    }

    @Test
    void shouldDeletePasswordSuccessfully() {
        UUID passwordId = UUID.randomUUID();
        Password password = new Password("Test Label", "Test Password", user);
        when(passwordRepository.findByIdAndOwnerId(passwordId, userId)).thenReturn(Optional.of(password));
        passwordService.deletePassword(passwordId, userId.toString());
        verify(passwordRepository, times(1)).delete(password);
    }

}

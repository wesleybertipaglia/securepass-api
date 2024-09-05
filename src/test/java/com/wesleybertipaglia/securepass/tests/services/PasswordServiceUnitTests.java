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

public class PasswordServiceUnitTests {

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
        // initialize mocks and test data before each test
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        user = new User("Test User", "test@example.com", "encodedPassword");
        user.setId(userId);
    }

    @Test
    void shouldCreatePasswordSuccessfully() {
        // arrange
        PasswordRequestRecord request = new PasswordRequestRecord("Test Label", "Test Password");
        Password password = new Password("Test Label", "Test Password", user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        try (MockedStatic<PasswordMapper> mockedMapper = mockStatic(PasswordMapper.class)) {
            mockedMapper.when(() -> PasswordMapper.requestRecordToEntity(request, user)).thenReturn(password);
            mockedMapper.when(() -> PasswordMapper.entityToResponseRecord(password))
                    .thenReturn(new PasswordResponseRecord(password.getId(), password.getLabel(),
                            password.getPassword(), null));

            when(passwordRepository.save(any(Password.class))).thenReturn(password);

            // act
            PasswordResponseRecord response = passwordService.createPassword(request, userId.toString());

            // assert
            assertNotNull(response, "Response should not be null");
            assertEquals("Test Label", response.label(), "Label should match");
            assertEquals("Test Password", response.password(), "Password should match");
        }
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnCreate() {
        // arrange
        PasswordRequestRecord request = new PasswordRequestRecord("Test Label", "Test Password");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(EntityNotFoundException.class, () -> passwordService.createPassword(request, userId.toString()),
                "Expected EntityNotFoundException when user is not found");
    }

    @Test
    void shouldListPasswordsSuccessfully() {
        // arrange
        Password password = new Password("Test Label", "Test Password", user);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Password> passwordPage = new PageImpl<>(List.of(password));
        when(passwordRepository.findAllByOwnerId(userId, pageable)).thenReturn(passwordPage);

        try (MockedStatic<PasswordMapper> mockedMapper = mockStatic(PasswordMapper.class)) {
            mockedMapper.when(() -> PasswordMapper.entityToResponseRecord(password))
                    .thenReturn(new PasswordResponseRecord(password.getId(), password.getLabel(),
                            password.getPassword(), null));

            // act
            Page<PasswordResponseRecord> response = passwordService.listPasswords(0, 10, userId.toString());

            // assert
            assertNotNull(response, "Response should not be null");
            assertEquals(1, response.getTotalElements(), "There should be one password listed");
            assertEquals("Test Label", response.getContent().get(0).label(), "Label should match");
        }
    }

    @Test
    void shouldGetPasswordSuccessfully() {
        // arrange
        UUID passwordId = UUID.randomUUID();
        Password password = new Password("Test Label", "Test Password", user);
        when(passwordRepository.findByIdAndOwnerId(passwordId, userId)).thenReturn(Optional.of(password));

        try (MockedStatic<PasswordMapper> mockedMapper = mockStatic(PasswordMapper.class)) {
            mockedMapper.when(() -> PasswordMapper.entityToResponseRecord(password))
                    .thenReturn(new PasswordResponseRecord(password.getId(), password.getLabel(),
                            password.getPassword(), null));

            // act
            PasswordResponseRecord response = passwordService.getPassword(passwordId, userId.toString());

            // assert
            assertNotNull(response, "Response should not be null");
            assertEquals("Test Label", response.label(), "Label should match");
        }
    }

    @Test
    void shouldThrowExceptionWhenPasswordNotFound() {
        // arrange
        UUID passwordId = UUID.randomUUID();
        when(passwordRepository.findByIdAndOwnerId(passwordId, userId)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(EntityNotFoundException.class, () -> passwordService.getPassword(passwordId, userId.toString()),
                "Expected EntityNotFoundException when password is not found");
    }

    @Test
    void shouldUpdatePasswordSuccessfully() {
        // arrange
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

            // act
            PasswordResponseRecord response = passwordService.updatePassword(passwordId, request, userId.toString());

            // assert
            assertNotNull(response, "Response should not be null");
            assertEquals("Updated Label", response.label(), "Updated label should match");
            assertEquals("Updated Password", response.password(), "Updated password should match");
        }
    }

    @Test
    void shouldDeletePasswordSuccessfully() {
        // arrange
        UUID passwordId = UUID.randomUUID();
        Password password = new Password("Test Label", "Test Password", user);
        when(passwordRepository.findByIdAndOwnerId(passwordId, userId)).thenReturn(Optional.of(password));

        // act
        passwordService.deletePassword(passwordId, userId.toString());

        // assert
        verify(passwordRepository, times(1)).delete(password);
    }

}
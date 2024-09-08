package com.wesleybertipaglia.securepass.services.password;

import com.wesleybertipaglia.securepass.entities.Password;
import com.wesleybertipaglia.securepass.entities.User;
import com.wesleybertipaglia.securepass.records.password.PasswordRequestRecord;
import com.wesleybertipaglia.securepass.records.password.PasswordResponseRecord;
import com.wesleybertipaglia.securepass.repositories.PasswordRepository;
import com.wesleybertipaglia.securepass.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {

    private static final UUID PASSWORD_ID = UUID.randomUUID();
    private static final String PASSWORD_VALUE = "P@ssw0rd123";
    private static final String PASSWORD_LABEL = "My Password";

    @InjectMocks
    private PasswordService passwordService;

    @Mock
    private PasswordRepository passwordRepository;

    @Mock
    private UserRepository userRepository;

    private User user;

    private Password password;

    @BeforeEach
    void setUp() {
        user = new User(UUID.randomUUID(), "User Name", "user@email.com", "U$&rP4s$w0r#");
        password = new Password(PASSWORD_ID, PASSWORD_LABEL, PASSWORD_VALUE, user);
    }

    @Nested
    class createPassword {
        @Test
        @DisplayName("Should create password successfully")
        void shouldCreatePasswordSuccessfully() {
            // arrange
            PasswordRequestRecord request = new PasswordRequestRecord(PASSWORD_LABEL, PASSWORD_VALUE);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(passwordRepository.save(any(Password.class))).thenReturn(password);

            // act
            PasswordResponseRecord response = passwordService.createPassword(request, user.getId().toString());

            // assert
            assertNotNull(response, "Created password should not be null");
            assertEquals(PASSWORD_ID, response.id(), "Password ID should match");
            assertEquals(PASSWORD_VALUE, response.password(), "Password value should match");
            verify(passwordRepository, times(1)).save(any(Password.class));
        }

        @Test
        @DisplayName("Should throw exception when user not found on create")
        void shouldThrowExceptionWhenUserNotFoundOnCreate() {
            // arrange
            PasswordRequestRecord request = new PasswordRequestRecord(PASSWORD_LABEL, PASSWORD_VALUE);
            when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

            // act & assert
            assertThrows(EntityNotFoundException.class, () -> passwordService.createPassword(request, user.getId().toString()),
                    "Should throw EntityNotFoundException when user is not found");
        }
    }

    @Nested
    class listPasswords {
        @Test
        @DisplayName("Should list passwords successfully")
        void shouldListPasswordsSuccessfully() {
            // arrange
            Pageable pageable = PageRequest.of(0, 1);
            Page<Password> passwordPage = new PageImpl<>(List.of(password), pageable, 1);
            when(passwordRepository.findAllByOwnerId(user.getId(), pageable)).thenReturn(passwordPage);

            // act
            Page<PasswordResponseRecord> passwords = passwordService.listPasswords(0, 1, user.getId().toString());

            // assert
            assertNotNull(passwords, "Password list should not be null");
            assertEquals(1, passwords.getTotalElements(), "Password list size should match");
            assertEquals(PASSWORD_VALUE, passwords.getContent().get(0).password(), "Password value should match");
            verify(passwordRepository, times(1)).findAllByOwnerId(user.getId(), pageable);
        }

        @Test
        @DisplayName("Should throw exception when page size is 0")
        void shouldThrowExceptionWhenPageSizeIs0() {
            // act & assert
            assertThrows(IllegalArgumentException.class, () -> passwordService.listPasswords(0, 0, user.getId().toString()),
                    "Should throw IllegalArgumentException when page size is 0");
        }
    }

    @Nested
    class getPassword {
        @Test
        @DisplayName("Should get password successfully")
        void shouldGetPasswordSuccessfully() {
            // arrange
            when(passwordRepository.findByIdAndOwnerId(PASSWORD_ID, user.getId())).thenReturn(Optional.of(password));

            // act
            PasswordResponseRecord response = passwordService.getPassword(PASSWORD_ID, user.getId().toString());

            // assert
            assertNotNull(response, "Retrieved password should not be null");
            assertEquals(PASSWORD_VALUE, response.password(), "Password value should match");
            verify(passwordRepository, times(1)).findByIdAndOwnerId(PASSWORD_ID, user.getId());
        }

        @Test
        @DisplayName("Should throw exception when password not found")
        void shouldThrowExceptionWhenPasswordNotFound() {
            // arrange
            when(passwordRepository.findByIdAndOwnerId(PASSWORD_ID, user.getId())).thenReturn(Optional.empty());

            // act & assert
            assertThrows(EntityNotFoundException.class, () -> passwordService.getPassword(PASSWORD_ID, user.getId().toString()),
                    "Should throw EntityNotFoundException when password is not found");
        }
    }

    @Nested
    class updatePassword {
        @Test
        @DisplayName("Should update password successfully")
        void shouldUpdatePasswordSuccessfully() {
            // arrange
            Password updatedPassword = new Password(PASSWORD_ID, PASSWORD_LABEL, PASSWORD_VALUE, user);
            PasswordRequestRecord passwordRequestRecord = new PasswordRequestRecord(PASSWORD_LABEL, PASSWORD_VALUE);
            when(passwordRepository.findById(PASSWORD_ID)).thenReturn(Optional.of(password));
            when(passwordRepository.save(any(Password.class))).thenReturn(updatedPassword);

            // act
            PasswordResponseRecord response = passwordService.updatePassword(PASSWORD_ID, passwordRequestRecord, user.getId().toString());

            // assert
            assertNotNull(response, "Updated password should not be null");
            assertEquals(updatedPassword.getPassword(), response.password(), "Updated password value should match");
            verify(passwordRepository, times(1)).findById(PASSWORD_ID);
            verify(passwordRepository, times(1)).save(any(Password.class));
        }

        @Test
        @DisplayName("Should throw exception when updating password that does not exist")
        void shouldThrowExceptionWhenUpdatingPasswordThatDoesNotExist() {
            // arrange
            PasswordRequestRecord passwordRequestRecord = new PasswordRequestRecord(PASSWORD_LABEL, PASSWORD_VALUE);
            when(passwordRepository.findById(PASSWORD_ID)).thenReturn(Optional.empty());

            // act & assert
            assertThrows(EntityNotFoundException.class, () -> passwordService.updatePassword(PASSWORD_ID, passwordRequestRecord, user.getId().toString()),
                    "Should throw EntityNotFoundException when password is not found");
        }
    }

    @Nested
    class deletePassword {
        @Test
        @DisplayName("Should delete password successfully")
        void shouldDeletePasswordSuccessfully() {
            // arrange
            when(passwordRepository.findByIdAndOwnerId(PASSWORD_ID, user.getId())).thenReturn(Optional.of(password));
            doNothing().when(passwordRepository).delete(password);

            // act
            passwordService.deletePassword(PASSWORD_ID, user.getId().toString());

            // assert
            verify(passwordRepository, times(1)).delete(password);
        }

        @Test
        @DisplayName("Should throw exception when deleting password that does not exist")
        void shouldThrowExceptionWhenDeletingPasswordThatDoesNotExist() {
            // arrange
            when(passwordRepository.findByIdAndOwnerId(PASSWORD_ID, user.getId())).thenReturn(Optional.empty());

            // act & assert
            assertThrows(EntityNotFoundException.class, () -> passwordService.deletePassword(PASSWORD_ID, user.getId().toString()),
                    "Should throw EntityNotFoundException when password is not found");
        }
    }

}
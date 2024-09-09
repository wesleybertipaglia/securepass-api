package com.wesleybertipaglia.securepass.controllers;

import com.wesleybertipaglia.securepass.entities.Password;
import com.wesleybertipaglia.securepass.entities.User;
import com.wesleybertipaglia.securepass.records.password.PasswordRequestRecord;
import com.wesleybertipaglia.securepass.records.password.PasswordResponseRecord;
import com.wesleybertipaglia.securepass.services.password.PasswordService;
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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PasswordControllerTest {

    private User user;
    private Password password;
    private JwtAuthenticationToken TOKEN;

    @InjectMocks
    private PasswordController passwordController;

    @Mock
    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        user = new User(UUID.randomUUID(), "User Name", "user@email.com", "U$&rP4s$w0r#");
        password = new Password(UUID.randomUUID(), "Password Label", "Password Value", user);
        TOKEN = mock(JwtAuthenticationToken.class);
        when(TOKEN.getName()).thenReturn(user.getId().toString());
    }

    @Nested
    class CreatePassword {
        @Test
        @DisplayName("Should create password successfully")
        void shouldCreatePasswordSuccessfully() {
            // arrange
            PasswordRequestRecord request = new PasswordRequestRecord(password.getLabel(), password.getPassword());
            PasswordResponseRecord expectedResponse = new PasswordResponseRecord(password.getId(), password.getLabel(), password.getPassword(), null);
            when(passwordService.createPassword(request, user.getId().toString())).thenReturn(expectedResponse);

            // act
            PasswordResponseRecord response = passwordController.createPassword(request, TOKEN).getBody();

            // assert
            assertNotNull(response, "Created password should not be null");
            assertEquals(expectedResponse.id(), response.id(), "Password ID should match");
            assertEquals(expectedResponse.password(), response.password(), "Password value should match");
        }

        @Test
        @DisplayName("Should throw exception when user not found on create")
        void shouldThrowExceptionWhenUserNotFoundOnCreate() {
            // arrange
            PasswordRequestRecord request = new PasswordRequestRecord(password.getLabel(), password.getPassword());
            when(passwordService.createPassword(request, user.getId().toString())).thenThrow(
                    new EntityNotFoundException("User not found"));

            // act & assert
            assertThrows(EntityNotFoundException.class, () -> passwordController.createPassword(request, TOKEN),
                    "Should throw EntityNotFoundException when user is not found");
        }
    }

    @Nested
    class ListPasswords {
        @Test
        @DisplayName("Should list passwords successfully")
        void shouldListPasswordsSuccessfully() {
            // arrange
            Pageable pageable = PageRequest.of(0, 1);
            Page<PasswordResponseRecord> passwordPage = new PageImpl<>(List.of(new PasswordResponseRecord(password.getId(), password.getLabel(), password.getPassword(), null)), pageable, 1);
            when(passwordService.listPasswords(0, 1, user.getId().toString())).thenReturn(passwordPage);

            // act
            Page<PasswordResponseRecord> responsePage = passwordController.listPasswords(0, 1, TOKEN).getBody();

            // assert
            assertNotNull(responsePage, "Password list should not be null");
            assertEquals(1, responsePage.getTotalElements(), "Password list size should match");
            assertEquals(password.getPassword(), responsePage.getContent().get(0).password(), "Password value should match");
        }

        @Test
        @DisplayName("Should throw exception when page size is 0")
        void shouldThrowExceptionWhenPageSizeIs0() {
            // arrange
            when(passwordController.listPasswords(0, 0, TOKEN)).thenCallRealMethod();

            // act & assert
            assertThrows(IllegalArgumentException.class, () -> passwordController.listPasswords(0, 0, TOKEN),
                    "Should throw IllegalArgumentException when page size is 0");
        }
    }

    @Nested
    class GetPassword {
        @Test
        @DisplayName("Should get password successfully")
        void shouldGetPasswordSuccessfully() {
            // arrange
            PasswordResponseRecord expectedResponse = new PasswordResponseRecord(password.getId(), password.getLabel(), password.getPassword(), null);
            when(passwordService.getPassword(password.getId(), user.getId().toString())).thenReturn(expectedResponse);

            // act
            PasswordResponseRecord response = passwordController.getPassword(password.getId(), TOKEN).getBody();

            // assert
            assertNotNull(response, "Retrieved password should not be null");
            assertEquals(expectedResponse.password(), response.password(), "Password value should match");
        }

        @Test
        @DisplayName("Should throw exception when password not found")
        void shouldThrowExceptionWhenPasswordNotFound() {
            // arrange
            when(passwordService.getPassword(any(UUID.class), anyString())).thenThrow(
                    new EntityNotFoundException("Password not found"));

            // act & assert
            assertThrows(EntityNotFoundException.class, () -> passwordController.getPassword(password.getId(), TOKEN),
                    "Should throw EntityNotFoundException when password is not found");
        }
    }

    @Nested
    class UpdatePassword {
        @Test
        @DisplayName("Should update password successfully")
        void shouldUpdatePasswordSuccessfully() {
            // arrange
            PasswordRequestRecord request = new PasswordRequestRecord(password.getLabel(), password.getPassword());
            PasswordResponseRecord expectedResponse = new PasswordResponseRecord(password.getId(), password.getLabel(), password.getPassword(), null);
            when(passwordService.updatePassword(password.getId(), request, user.getId().toString())).thenReturn(expectedResponse);

            // act
            PasswordResponseRecord response = passwordController.updatePassword(password.getId(), request, TOKEN).getBody();

            // assert
            assertNotNull(response, "Updated password should not be null");
            assertEquals(expectedResponse.password(), response.password(), "Updated password value should match");
        }

        @Test
        @DisplayName("Should throw exception when updating password that does not exist")
        void shouldThrowExceptionWhenUpdatingPasswordThatDoesNotExist() {
            // arrange
            PasswordRequestRecord request = new PasswordRequestRecord(password.getLabel(), password.getPassword());
            when(passwordService.updatePassword(any(UUID.class), any(), anyString())).thenThrow(
                    new EntityNotFoundException("Password not found"));

            // act & assert
            assertThrows(EntityNotFoundException.class, () -> passwordController.updatePassword(password.getId(), request, TOKEN),
                    "Should throw EntityNotFoundException when updating password that does not exist");
        }
    }

    @Nested
    class DeletePassword {
        @Test
        @DisplayName("Should delete password successfully")
        void shouldDeletePasswordSuccessfully() {
            // arrange
            doNothing().when(passwordService).deletePassword(any(UUID.class), anyString());

            // act
            passwordController.deletePassword(password.getId(), TOKEN);

            // assert
            verify(passwordService, times(1)).deletePassword(password.getId(), user.getId().toString());
        }

        @Test
        @DisplayName("Should throw exception when deleting password that does not exist")
        void shouldThrowExceptionWhenDeletingPasswordThatDoesNotExist() {
            // arrange
            doThrow(new EntityNotFoundException("Password not found")).when(passwordService).deletePassword(any(UUID.class), anyString());

            // act & assert
            assertThrows(EntityNotFoundException.class, () -> passwordController.deletePassword(password.getId(), TOKEN),
                    "Should throw EntityNotFoundException when deleting password that does not exist");
        }
    }

}
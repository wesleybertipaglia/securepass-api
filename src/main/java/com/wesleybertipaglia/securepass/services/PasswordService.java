package com.wesleybertipaglia.securepass.services;

import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

import com.wesleybertipaglia.securepass.entities.User;
import com.wesleybertipaglia.securepass.entities.Password;
import com.wesleybertipaglia.securepass.repositories.PasswordRepository;
import com.wesleybertipaglia.securepass.repositories.UserRepository;
import com.wesleybertipaglia.securepass.records.password.PasswordRequestRecord;
import com.wesleybertipaglia.securepass.records.password.PasswordResponseRecord;

@Service
public class PasswordService {
    @Autowired
    private PasswordRepository passwordRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public PasswordResponseRecord createPassword(PasswordRequestRecord passwordRequest, String tokenSubject) {
        User user = userRepository.findById(UUID.fromString(tokenSubject))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Password password = passwordRepository.save(
                new Password(passwordRequest.label(), passwordRequest.password(), user));

        return new PasswordResponseRecord(password.getId(), password.getLabel(),
                password.getPassword());
    }

    @Transactional(readOnly = true)
    public Page<PasswordResponseRecord> listPasswords(int page, int size, String tokenSubject) {
        Pageable pageable = PageRequest.of(page, size);

        return passwordRepository.findAllByOwnerId(UUID.fromString(tokenSubject), pageable).map(
                password -> new PasswordResponseRecord(password.getId(), password.getLabel(), password.getPassword()));
    }

    @Transactional(readOnly = true)
    public PasswordResponseRecord getPassword(UUID id) {
        Password password = passwordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Password not found"));

        return new PasswordResponseRecord(password.getId(), password.getLabel(), password.getPassword());
    }

    @Transactional
    public PasswordResponseRecord updatePassword(UUID id, PasswordRequestRecord passwordRequest, String tokenSubject) {
        Password password = passwordRepository.findById(id)
                .orElseThrow(() -> new AccessDeniedException("Password not found"));

        if (!password.getOwner().getId().equals(UUID.fromString(tokenSubject))) {
            throw new AccessDeniedException("You are not allowed to update this password");
        }

        if (passwordRequest.label() != null) {
            password.setLabel(passwordRequest.label());
        }

        if (passwordRequest.password() != null) {
            password.setPassword(passwordRequest.password());
        }

        return new PasswordResponseRecord(password.getId(), password.getLabel(), password.getPassword());
    }

    @Transactional
    public void deletePassword(UUID id, String tokenSubject) {
        Password password = passwordRepository.findByIdAndOwnerId(id, UUID.fromString(tokenSubject))
                .orElseThrow(() -> new EntityNotFoundException("Password not found"));

        passwordRepository.delete(password);
    }

}

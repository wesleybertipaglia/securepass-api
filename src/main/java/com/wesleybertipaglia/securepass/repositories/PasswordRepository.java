package com.wesleybertipaglia.securepass.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wesleybertipaglia.securepass.entities.Password;

public interface PasswordRepository extends JpaRepository<Password, UUID> {
    Optional<Password> findByIdAndOwnerId(UUID id, UUID ownerId);

    Boolean existsByIdAndOwnerId(UUID id, UUID ownerId);

    Page<Password> findAllByOwnerId(UUID ownerId, Pageable pageable);
}
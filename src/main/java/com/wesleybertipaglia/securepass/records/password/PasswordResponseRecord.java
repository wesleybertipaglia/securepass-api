package com.wesleybertipaglia.securepass.records.password;

import java.util.UUID;

import org.springframework.hateoas.Links;

import jakarta.validation.constraints.NotBlank;

public record PasswordResponseRecord(
        UUID id,
        @NotBlank(message = "Label cannot be blank") String label,
        @NotBlank(message = "Password cannot be blank") String password,
        Links links) {
}

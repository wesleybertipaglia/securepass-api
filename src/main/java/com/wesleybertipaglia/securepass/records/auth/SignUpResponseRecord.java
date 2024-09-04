package com.wesleybertipaglia.securepass.records.auth;

import jakarta.validation.constraints.*;

public record SignUpResponseRecord(
        @NotBlank(message = "Name cannot be blank") String name,
        @NotBlank(message = "E-mail cannot be blank") @Email(message = "Email must be valid") @Size(max = 100, message = "Email must be less than 100 characters") String email) {
}
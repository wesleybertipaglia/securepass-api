package com.wesleybertipaglia.securepass.records.auth;

import jakarta.validation.constraints.*;

public record SignInRequestRecord(
                @NotBlank(message = "Email cannot be blank") String email,
                @NotBlank(message = "Password cannot be blank") String password) {
}
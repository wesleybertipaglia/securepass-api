package com.wesleybertipaglia.securepass.records.auth;

import jakarta.validation.constraints.*;

public record SignInResponseRecord(
                @NotBlank(message = "Access token cannot be blank") String accessToken,
                @NotBlank(message = "Expires in cannot be blank") Long expiresIn) {
}
package com.wesleybertipaglia.securepass.records.password;

import jakarta.validation.constraints.NotBlank;

public record PasswordRequestRecord(
        @NotBlank(message = "Label cannot be blank") String label,
        @NotBlank(message = "Password cannot be blank") String password) {
}

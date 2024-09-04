package com.wesleybertipaglia.securepass.records.checker;

import jakarta.validation.constraints.NotBlank;

public record PasswordCheckerRequestRecord(
        @NotBlank(message = "Password cannot be blank") String password) {
}

package com.wesleybertipaglia.securepass.records.checker;

import java.util.List;

import jakarta.validation.constraints.*;

public record PasswordCheckerResponseRecord(
                @NotBlank(message = "Strength cannot be blank") String strength,
                @NotNull(message = "Suggestions cannot be null") List<String> suggestions) {
}

package com.wesleybertipaglia.securepass.records.password;

import java.util.UUID;

public record PasswordResponseRecord(UUID id, String label, String password) {

}

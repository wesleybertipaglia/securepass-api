package com.wesleybertipaglia.securepass.records.checker;

import java.util.List;

public record PasswordCheckerResponseRecord(String strength, List<String> suggestions) {
}

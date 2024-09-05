package com.wesleybertipaglia.securepass.services.generator;

import com.wesleybertipaglia.securepass.records.generator.PasswordGeneratorResponseRecord;

public interface PasswordGeneratorServiceInterface {
    PasswordGeneratorResponseRecord generatePassword(int length, boolean includeUppercase, boolean includeLowercase,
            boolean includeNumbers, boolean includeSpecial);
}

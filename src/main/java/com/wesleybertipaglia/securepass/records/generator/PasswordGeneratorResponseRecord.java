package com.wesleybertipaglia.securepass.records.generator;

public record PasswordGeneratorResponseRecord(String password, GenerationProperties properties) {

        public record GenerationProperties(int passwordLength, boolean includeUppercase,
                        boolean includeLowercase, boolean includeNumbers, boolean includeSpecial) {
        }
}

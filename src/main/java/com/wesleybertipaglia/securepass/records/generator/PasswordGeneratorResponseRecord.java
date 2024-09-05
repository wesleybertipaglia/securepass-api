package com.wesleybertipaglia.securepass.records.generator;

public record PasswordGeneratorResponseRecord(String password, GenerationProperties properties) {

        public record GenerationProperties(int length, boolean uppercase,
                        boolean lowercase, boolean numbers, boolean special) {
        }
}

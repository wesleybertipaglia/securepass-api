package com.wesleybertipaglia.securepass.records.auth;

public record SignInResponseRecord(String accessToken, Long expiresIn) {
}
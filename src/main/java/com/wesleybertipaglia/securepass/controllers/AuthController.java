package com.wesleybertipaglia.securepass.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.wesleybertipaglia.securepass.records.auth.SignInRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignInResponseRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpResponseRecord;
import com.wesleybertipaglia.securepass.services.auth.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public SignInResponseRecord signin(@RequestBody SignInRequestRecord signInRequest) {
        return authService.signIn(signInRequest);
    }

    @PostMapping("/signup")
    public SignUpResponseRecord signup(@RequestBody SignUpRequestRecord signUpRequest) {
        return authService.signUp(signUpRequest);
    }

    @DeleteMapping("/delete")
    public void delete(JwtAuthenticationToken token) {
        authService.deleteAccount(token.getName());
    }

}
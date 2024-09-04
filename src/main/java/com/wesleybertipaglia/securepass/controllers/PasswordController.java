package com.wesleybertipaglia.securepass.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wesleybertipaglia.securepass.services.PasswordService;
import com.wesleybertipaglia.securepass.records.password.PasswordRequestRecord;
import com.wesleybertipaglia.securepass.records.password.PasswordResponseRecord;

@RestController
@RequestMapping("/passwords")
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    @PostMapping
    public ResponseEntity<PasswordResponseRecord> createPassword(@RequestBody PasswordRequestRecord passwordRequest,
            JwtAuthenticationToken token) {
        return ResponseEntity.ok(passwordService.createPassword(passwordRequest, token.getName()));
    }

    @GetMapping
    public ResponseEntity<Page<PasswordResponseRecord>> listPasswords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            JwtAuthenticationToken token) {
        return ResponseEntity.ok(passwordService.listPasswords(page, size, token.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PasswordResponseRecord> getPassword(@PathVariable UUID id) {
        return ResponseEntity.ok(passwordService.getPassword(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PasswordResponseRecord> updatePassword(@PathVariable UUID id,
            @RequestBody PasswordRequestRecord passwordRequest,
            JwtAuthenticationToken token) {
        return ResponseEntity.ok(passwordService.updatePassword(id, passwordRequest, token.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassword(@PathVariable UUID id, JwtAuthenticationToken token) {
        passwordService.deletePassword(id, token.getName());
        return ResponseEntity.noContent().build();
    }
}

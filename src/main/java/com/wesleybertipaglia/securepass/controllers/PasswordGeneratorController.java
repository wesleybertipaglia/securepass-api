package com.wesleybertipaglia.securepass.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wesleybertipaglia.securepass.services.PasswordGeneratorService;
import com.wesleybertipaglia.securepass.records.generator.PasswordGeneratorResponseRecord;

@RestController
@RequestMapping("/generate")
public class PasswordGeneratorController {

    @Autowired
    private PasswordGeneratorService passwordGeneratorService;

    @GetMapping
    public ResponseEntity<PasswordGeneratorResponseRecord> generatePassword(
            @RequestParam(defaultValue = "12") int length,
            @RequestParam(defaultValue = "true") boolean uppercase,
            @RequestParam(defaultValue = "true") boolean lowercase,
            @RequestParam(defaultValue = "true") boolean numbers,
            @RequestParam(defaultValue = "true") boolean special) {
        return ResponseEntity
                .ok(passwordGeneratorService.generatePassword(length, uppercase, lowercase, numbers, special));
    }

}

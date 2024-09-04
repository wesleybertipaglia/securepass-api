package com.wesleybertipaglia.securepass.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wesleybertipaglia.securepass.services.PasswordCheckerService;
import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerRequestRecord;
import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerResponseRecord;

@RestController
@RequestMapping("/check")
public class PasswordCheckerController {

    @Autowired
    private PasswordCheckerService passwordCheckerService;

    @PostMapping
    public ResponseEntity<PasswordCheckerResponseRecord> checkPassword(
            @RequestBody PasswordCheckerRequestRecord passwordCheckerRequestRecord) {
        return ResponseEntity.ok(passwordCheckerService.checkPassword(passwordCheckerRequestRecord));
    }

}

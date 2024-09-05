package com.wesleybertipaglia.securepass.services.auth;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.wesleybertipaglia.securepass.entities.User;
import com.wesleybertipaglia.securepass.repositories.UserRepository;
import com.wesleybertipaglia.securepass.records.auth.SignInRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignInResponseRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpResponseRecord;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public SignInResponseRecord signIn(SignInRequestRecord signInRequest) {
        User user = userRepository.findByEmail(signInRequest.email()).orElseThrow(
                () -> new BadCredentialsException("Invalid e-mail or password."));

        if (!passwordEncoder.matches(signInRequest.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid e-mail or password.");
        }

        Instant now = Instant.now();
        Long expirationTime = Long.valueOf(24 * 60 * 60 * 1000);

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("securepass")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationTime))
                .claim("role", "USER")
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
        return new SignInResponseRecord(token, expirationTime);
    }

    @Transactional
    public SignUpResponseRecord signUp(SignUpRequestRecord signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.email())) {
            throw new BadCredentialsException("E-mail already in use.");
        }

        User user = new User(signUpRequest.name(), signUpRequest.email(),
                passwordEncoder.encode(signUpRequest.password()));

        userRepository.save(user);
        return new SignUpResponseRecord(user.getName(), user.getEmail());
    }

    @Transactional
    public void deleteAccount(String tokenSubject) {
        User user = userRepository.findById(UUID.fromString(tokenSubject)).orElseThrow(
                () -> new BadCredentialsException("Account not found."));
        userRepository.delete(user);
    }

}

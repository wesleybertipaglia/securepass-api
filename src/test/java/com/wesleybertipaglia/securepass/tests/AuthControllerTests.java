package com.wesleybertipaglia.securepass.tests;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wesleybertipaglia.securepass.configuration.SecurityConfig;
import com.wesleybertipaglia.securepass.controllers.AuthController;
import com.wesleybertipaglia.securepass.records.auth.SignInRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignInResponseRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpResponseRecord;
import com.wesleybertipaglia.securepass.services.auth.AuthService;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSignInSuccessfully() throws Exception {
        SignInRequestRecord signInRequest = new SignInRequestRecord("test@example.com", "password123");
        SignInResponseRecord signInResponse = new SignInResponseRecord("mockedToken", 86400L);

        when(authService.signIn(signInRequest)).thenReturn(signInResponse);

        mockMvc.perform(post("/auth/signin")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mockedToken"))
                .andExpect(jsonPath("$.expiresIn").value(86400L));
    }

    @Test
    void shouldSignUpSuccessfully() throws Exception {
        SignUpRequestRecord signUpRequest = new SignUpRequestRecord("Test User", "test@example.com", "password123");
        SignUpResponseRecord signUpResponse = new SignUpResponseRecord("Test User", "test@example.com");
        when(authService.signUp(signUpRequest)).thenReturn(signUpResponse);

        mockMvc.perform(post("/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

}

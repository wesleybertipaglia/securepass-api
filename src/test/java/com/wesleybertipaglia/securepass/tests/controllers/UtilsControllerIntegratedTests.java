package com.wesleybertipaglia.securepass.tests.controllers;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wesleybertipaglia.securepass.configuration.SecurityConfig;
import com.wesleybertipaglia.securepass.controllers.UtilsController;
import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerRequestRecord;
import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerResponseRecord;
import com.wesleybertipaglia.securepass.records.generator.PasswordGeneratorResponseRecord;
import com.wesleybertipaglia.securepass.services.checker.PasswordCheckerService;
import com.wesleybertipaglia.securepass.services.generator.PasswordGeneratorService;

@WebMvcTest(UtilsController.class)
@Import(SecurityConfig.class)
public class UtilsControllerIntegratedTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordCheckerService passwordCheckerService;

    @MockBean
    private PasswordGeneratorService passwordGeneratorService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldCheckPassword() throws Exception {
        // arrange
        PasswordCheckerRequestRecord request = new PasswordCheckerRequestRecord("password123");
        PasswordCheckerResponseRecord response = new PasswordCheckerResponseRecord("Strong", List.of());

        when(passwordCheckerService.checkPassword(request)).thenReturn(response);

        // act & assert
        mockMvc.perform(post("/utils/checker")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.strength", is("Strong")))
                .andExpect(jsonPath("$.suggestions", is(List.of())));
    }

    @Test
    void shouldGeneratePassword() throws Exception {
        // arrange
        PasswordGeneratorResponseRecord response = new PasswordGeneratorResponseRecord(
                "generatedPassword",
                new PasswordGeneratorResponseRecord.GenerationProperties(12, true, true, true, true));

        when(passwordGeneratorService.generatePassword(12, true, true, true, true)).thenReturn(response);

        // act & assert
        mockMvc.perform(get("/utils/generator")
                .param("length", "12")
                .param("uppercase", "true")
                .param("lowercase", "true")
                .param("numbers", "true")
                .param("special", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password", is("generatedPassword")))
                .andExpect(jsonPath("$.properties.length", is(12)))
                .andExpect(jsonPath("$.properties.uppercase", is(true)))
                .andExpect(jsonPath("$.properties.lowercase", is(true)))
                .andExpect(jsonPath("$.properties.numbers", is(true)))
                .andExpect(jsonPath("$.properties.special", is(true)));
    }

}
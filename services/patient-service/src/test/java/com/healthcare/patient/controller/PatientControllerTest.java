package com.healthcare.patient.controller;

import com.healthcare.patient.dto.PatientRegistrationRequest;
import com.healthcare.patient.dto.PatientResponse;
import com.healthcare.patient.entity.Gender;
import com.healthcare.patient.exception.ResourceNotFoundException;
import com.healthcare.patient.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
public class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private PatientService patientService;

    @Test
    void shouldReturn201WhenPatientIsRegistered() throws Exception {
        UUID patientId = UUID.randomUUID();

        PatientRegistrationRequest request =
                new PatientRegistrationRequest(
                        "Aarav",
                        "Sharma",
                        LocalDate.of(1995, 6, 15),
                        Gender.MALE,
                        "aarav@example.com",
                        "9876543210"
                );

        PatientResponse response = new PatientResponse(
                patientId,
                "PAT-K7M4-X9Q2",
                "Aarav",
                "Sharma",
                LocalDate.of(1995, 6, 15),
                Gender.MALE,
                "aarav@example.com",
                "9876543210",
                Instant.parse("2026-01-01T10:00:00Z")
        );

        when(patientService.registerPatient(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location", "/api/v1/patients/" + patientId))

                .andExpect(jsonPath("$.id").value(patientId.toString()))
                .andExpect(jsonPath("$.medicalRecordNumber")
                        .value("PAT-K7M4-X9Q2"))
                .andExpect(jsonPath("$.firstName").value("Aarav"));
    }

    @Test
    void shouldReturn400WhenFirstNameIsMissing() throws Exception {
        String invalidRequest = """
        {
          "lastName": "Sharma",
          "dateOfBirth": "1995-06-15",
          "gender": "MALE"
        }
        """;

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.firstName").exists());
    }

    @Test
    void shouldReturn200WhenPatientExists() throws Exception {
        UUID patientId = UUID.randomUUID();

        PatientResponse response = new PatientResponse(
                patientId,
                "PAT-K7M4-X9Q2",
                "Aarav",
                "Sharma",
                LocalDate.of(1995, 6, 15),
                Gender.MALE,
                "aarav@example.com",
                "9876543210",
                Instant.parse("2026-01-01T10:00:00Z")
        );

        when(patientService.getPatientById(patientId))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/patients/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientId.toString()))
                .andExpect(jsonPath("$.medicalRecordNumber")
                        .value("PAT-K7M4-X9Q2"))
                .andExpect(jsonPath("$.firstName").value("Aarav"));
    }


    @Test
    void shouldReturn404WhenPatientDoesNotExist() throws Exception {
        UUID patientId = UUID.randomUUID();

        when(patientService.getPatientById(patientId))
                .thenThrow(new ResourceNotFoundException(
                        "Patient not found with id: " + patientId));

        mockMvc.perform(get("/api/v1/patients/{id}", patientId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Patient not found with id: " + patientId));
    }

    @Test
    void shouldReturn400WhenPatientIdIsMalformed() throws Exception {
        mockMvc.perform(get("/api/v1/patients/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}

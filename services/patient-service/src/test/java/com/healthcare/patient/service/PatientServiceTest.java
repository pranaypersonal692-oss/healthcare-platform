package com.healthcare.patient.service;

import com.healthcare.patient.dto.PatientRegistrationRequest;
import com.healthcare.patient.dto.PatientResponse;
import com.healthcare.patient.entity.Gender;
import com.healthcare.patient.entity.Patient;
import com.healthcare.patient.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MedicalRecordNumberGenerator medicalRecordNumberGenerator;

    @InjectMocks
    private PatientService patientService;

    @Test
    void shouldRegisterPatientSuccessfully() {
        // Arrange
        UUID patientId = UUID.randomUUID();
        String mrn = "PAT-K7M4-X9Q2";

        PatientRegistrationRequest request =
                new PatientRegistrationRequest(
                        "Aarav",
                        "Sharma",
                        LocalDate.of(1995, 6, 15),
                        Gender.MALE,
                        "aarav@example.com",
                        "9876543210"
                );

        when(medicalRecordNumberGenerator.generate()).thenReturn(mrn);
        when(patientRepository.saveAndFlush(any(Patient.class)))
                .thenAnswer(invocation -> {
                    Patient patient = invocation.getArgument(0);
                    patient.setId(patientId);
                    return patient;
                });

        // Act
        PatientResponse response = patientService.registerPatient(request);

        // Assert
        assertNotNull(response);
        assertEquals(patientId, response.id());
        assertEquals(mrn, response.medicalRecordNumber());
        assertEquals("Aarav", response.firstName());
        assertEquals("Sharma", response.lastName());
        assertEquals(Gender.MALE, response.gender());

        verify(medicalRecordNumberGenerator).generate();
        verify(patientRepository).saveAndFlush(any(Patient.class));
    }

    @Test
    void shouldNormalizeBlankOptionalFieldsToNull() {
        // Arrange
        PatientRegistrationRequest request =
                new PatientRegistrationRequest(
                        "Aarav",
                        "Sharma",
                        LocalDate.of(1995, 6, 15),
                        Gender.MALE,
                        "   ",
                        ""
                );
        when(medicalRecordNumberGenerator.generate())
                .thenReturn("PAT-K7M4-X9Q2");

        when(patientRepository.saveAndFlush(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PatientResponse response =
                patientService.registerPatient(request);

        // Assert
        assertNull(response.email());
        assertNull(response.phone());
    }


}

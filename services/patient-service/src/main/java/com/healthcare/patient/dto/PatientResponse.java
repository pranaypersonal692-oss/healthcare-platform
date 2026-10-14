package com.healthcare.patient.dto;

import com.healthcare.patient.entity.Gender;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PatientResponse (
        UUID id,
        String medicalRecordNumber,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Gender gender,
        String email,
        String phone,
        Instant createdAt
) {}

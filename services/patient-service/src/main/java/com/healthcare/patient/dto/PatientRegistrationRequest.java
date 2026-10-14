package com.healthcare.patient.dto;


import com.healthcare.patient.entity.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PatientRegistrationRequest (
        @NotBlank
        @Size(max = 100)
        String firstName,

        @NotNull
        @Size(max = 100)
        String lastName,

        @NotNull
        @PastOrPresent
        LocalDate dateOfBirth,

        @NotNull
        Gender gender,

        @Email
        @Size(max = 255)
        String email,

        @Size(max = 20)
        String phone
){}

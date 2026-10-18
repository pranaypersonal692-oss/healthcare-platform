package com.healthcare.patient.service;

import com.healthcare.patient.dto.PatientRegistrationRequest;
import com.healthcare.patient.dto.PatientResponse;
import com.healthcare.patient.dto.PatientUpdateRequest;
import com.healthcare.patient.entity.Patient;
import com.healthcare.patient.exception.ResourceNotFoundException;
import com.healthcare.patient.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final MedicalRecordNumberGenerator mrnGenerator;

    public PatientService(
            PatientRepository patientRepository,
            MedicalRecordNumberGenerator mrnGenerator
    ) {
        this.patientRepository = patientRepository;
        this.mrnGenerator = mrnGenerator;
    }

    @Transactional
    public PatientResponse registerPatient(PatientRegistrationRequest request) {
        Patient patient = new Patient();

        patient.setId(UUID.randomUUID());
        patient.setMedicalRecordNumber(mrnGenerator.generate());
        patient.setFirstName(request.firstName().trim());
        patient.setLastName(request.lastName().trim());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setEmail(normalizeOptional(request.email()));
        patient.setPhone(normalizeOptional(request.phone()));

        Patient saved = patientRepository.saveAndFlush(patient);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(UUID id){
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + id
                ));
        return toResponse(patient);
    }

    @Transactional
    public PatientResponse updatePatient(
            UUID patientId,
            PatientUpdateRequest request) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found with id: " + patientId));

        patient.setFirstName(request.firstName().trim());
        patient.setLastName(request.lastName().trim());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setEmail(normalizeOptional(request.email()));
        patient.setPhone(normalizeOptional(request.phone()));

        Patient updatedPatient = patientRepository.saveAndFlush(patient);
        return toResponse(updatedPatient);
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getMedicalRecordNumber(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getCreatedAt()
        );
    }
}


package com.healthcare.patient.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class MedicalRecordNumberGenerator {
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int GROUP_LENGTH = 4;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        return "PAT-" + randomGroup() + "-" + randomGroup();
    }

    private String randomGroup() {
        StringBuilder group = new StringBuilder(GROUP_LENGTH);

        for(int i = 0; i < GROUP_LENGTH; i++) {
            group.append(CHARACTERS.charAt(
                    random.nextInt(CHARACTERS.length())
            ));
        }
        return group.toString();
    }
}

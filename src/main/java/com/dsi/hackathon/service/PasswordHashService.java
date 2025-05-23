package com.dsi.hackathon.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordHashService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordHashService.class);

    private final PasswordEncoder passwordEncoder;

    public PasswordHashService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String getPasswordHash(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Password value must not be null or empty");
        }

        return passwordEncoder.encode(rawValue);
    }

    public boolean passwordHashMatches(String rawValue, String hashValue) {
        if (hashValue == null || rawValue == null) {
            return false;
        }

        return passwordEncoder.matches(rawValue, hashValue);
    }
}

package com.dsi.hackathon.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordHashService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordHashService.class);

    private final PasswordEncoder passwordEncoder;

    public PasswordHashService(@Value("${security.password.secret}") String secretKey) {
        this.passwordEncoder = buildPasswordEncoder(secretKey);
    }

    private PasswordEncoder buildPasswordEncoder(String secretKey) {
        Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(
            secretKey,
            128,
            1000,
            Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256
        );

        encoder.setEncodeHashAsBase64(true);

        return encoder;
    }

    public String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password must not be null or empty");
        }

        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return rawPassword != null && encodedPassword != null && passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

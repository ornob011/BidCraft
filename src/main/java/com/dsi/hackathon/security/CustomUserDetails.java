package com.dsi.hackathon.security;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Objects;

@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final transient com.dsi.hackathon.entity.User user;

    public CustomUserDetails(com.dsi.hackathon.entity.User user) {
        super(user.getEmail(), user.getPassword(), new ArrayList<>());
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomUserDetails that)) return false;
        return Objects.equals(user.getId(), that.user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(user.getId());
    }
}


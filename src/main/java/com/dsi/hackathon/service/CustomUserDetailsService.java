package com.dsi.hackathon.service;

import com.dsi.hackathon.entity.User;
import com.dsi.hackathon.repository.UserRepository;
import com.dsi.hackathon.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                                  .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));

        return new CustomUserDetails(user);
    }
}

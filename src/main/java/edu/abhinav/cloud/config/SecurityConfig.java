package edu.abhinav.cloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean authenticatePassword(String userPassword, String hashPassword) {
        return passwordEncoder.matches(userPassword, hashPassword);
    }
}

package org.example.namia.supportdeskspringboot.config;

import org.example.namia.supportdeskspringboot.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
//        // This encodes the raw string "password" directly at startup
//        // to match against your plain text input during login
//        return new InMemoryUserDetailsManager(
//                User.withUsername("username")
//                        .password(encoder.encode("password"))
//                        .roles("USER")
//                        .build()
//        );
//    }

    @Bean
    public UserDetailsService userDetailsService(
            UserRepository userRepository
    ) {
        return email -> {
            // 1. Fetch your custom database User entity
            org.example.namia.supportdeskspringboot.model.entity.User dbUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            // 2. Map it to Spring Security's internal User object
            return org.springframework.security.core.userdetails.User
                    .withUsername(dbUser.getEmail()) // We treat the Email as the "username" for authentication
                    .password(dbUser.getPasswordHash())
                    .roles(dbUser.getRole().name()) // Assuming Roles is an enum
                    .disabled(!dbUser.getEnabled())
                    .build();
        };
    }
}

package org.plurb.panorama.config;

import org.plurb.panorama.model.User;
import org.plurb.panorama.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername("Benny")) {
            return;
        }
        User user = new User();
        user.setUsername("chenbenny31");
        user.setPasswordHash(passwordEncoder.encode("CHANGEME"));
        user.setDisplayName("Benny");
        userRepository.save(user);
        System.out.println("[DataInitializer] seed user created: Benny");
    }
}

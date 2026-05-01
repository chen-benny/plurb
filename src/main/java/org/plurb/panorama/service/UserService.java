package org.plurb.panorama.service;

import org.plurb.panorama.model.User;
import org.plurb.panorama.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void updateAbout(User user, String aboutMd) {
        user.setAboutMd(aboutMd);
        userRepository.save(user);
    }

    @Transactional
    public boolean changePassword(User user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            return false;
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Transactional
    public String changeUsername(User user, String newUsername, String password) {
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return "Current password is incorrect.";
        }
        String trimmed = newUsername.trim().toLowerCase();
        if (trimmed.equals(user.getUsername())) {
            return "That is already your username.";
        }
        if (userRepository.existsByUsername(trimmed)) {
            return "Username '" + trimmed + "' is already taken.";
        }
        user.setUsername(trimmed);
        userRepository.save(user);
        return null;
    }


}

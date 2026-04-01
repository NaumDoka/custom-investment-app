package com.investment_be.service;

import com.investment_be.model.User;
import com.investment_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User register(User user) {
        if (userRepo.findByName(user.getName()).isPresent()) {
            throw new RuntimeException("USERNAME_TAKEN");
        }

        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("EMAIL_TAKEN");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("CLIENT");
        return userRepo.save(user);
    }

    public String login(String name, String password) {
        User user = userRepo.findByName(name).orElse(null);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return jwtService.generateToken(user);
        }
        return null;
    }

    public User updateProfile(Long id, String name, String email, String phone, String role) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        return userRepo.save(user);
    }

    public User updateUserRole(Long id, String role) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        return userRepo.save(user);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User getUserByName(String name) {
        return userRepo.findByName(name).orElse(null);
    }

    public void deleteUserById(Long id) {
        userRepo.deleteById(id);
    }
}

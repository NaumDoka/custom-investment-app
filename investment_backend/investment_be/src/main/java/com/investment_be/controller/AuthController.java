package com.investment_be.controller;

import com.investment_be.model.User;
import com.investment_be.model.portfolio.UserSummaryDTO;
import com.investment_be.repository.UserRepository;
import com.investment_be.service.JwtService;
import com.investment_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            String token = jwtService.generateToken(registeredUser);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", registeredUser
            ));
        } catch (RuntimeException e) {
            if ("USERNAME_TAKEN".equals(e.getMessage())) {
                return ResponseEntity.status(409).body("Username is taken");
            }
            if ("EMAIL_TAKEN".equals(e.getMessage())) {
                return ResponseEntity.status(400).body("EMAIL_TAKEN");
            }
            return ResponseEntity.badRequest().body("Registration failed");
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam("name") String name) {
        boolean exists = userRepository.findByName(name).isPresent();
        return ResponseEntity.ok(!exists);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String token = userService.login(body.get("name"), body.get("password"));
        if (token != null) {
            User user = userService.getUserByName(body.get("name"));
            return ResponseEntity.ok(Map.of("token", token, "user", user));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PutMapping("/update-profile/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateProfile(id, userDetails.getName(), userDetails.getEmail(), userDetails.getPhone(), userDetails.getRole());
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/users-summary")
    public List<UserSummaryDTO> getPerformanceData() {
        return userRepository.findAllUserSummaries();
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}

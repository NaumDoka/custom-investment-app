package com.investment_be.controller;

import com.investment_be.model.User;
import com.investment_be.service.MarketSimulatorService;
import com.investment_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final MarketSimulatorService market;


    @PostMapping("/pump/{assetName}")
    public void pump(@PathVariable String assetName, @RequestParam double amount) {
        market.forceMoveSingle(assetName, amount);
    }

    @PostMapping("/dump/{assetName}")
    public void dump(@PathVariable String assetName, @RequestParam double amount) {
        market.forceMoveSingle(assetName, amount);
    }

    @PutMapping("/update-user-role/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUserRole(id, userDetails.getRole());
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete-user/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}

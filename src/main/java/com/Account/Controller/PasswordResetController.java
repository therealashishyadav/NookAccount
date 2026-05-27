package com.Account.Controller;

import com.Account.Services.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email is required"));
        }

        passwordResetService.sendOtp(email);

        // Always return success — don't reveal if email exists
        return ResponseEntity.ok(
                Map.of("message", "If this email is registered, an OTP has been sent.")
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");
        String otp   = request.get("otp");

        if (email == null || otp == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email and OTP are required"));
        }

        boolean valid = passwordResetService.verifyOtp(email, otp);

        if (!valid) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid or expired OTP. Please try again."));
        }

        return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestBody Map<String, String> request) {

        String email       = request.get("email");
        String otp         = request.get("otp");
        String newPassword = request.get("newPassword");

        if (email == null || otp == null || newPassword == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email, OTP and new password are required"));
        }

        if (newPassword.length() < 8) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Password must be at least 8 characters"));
        }

        boolean success = passwordResetService.resetPassword(email, otp, newPassword);

        if (!success) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "OTP expired or invalid. Please request a new one."));
        }

        return ResponseEntity.ok(Map.of("message", "Password reset successfully. Please log in."));
    }
}
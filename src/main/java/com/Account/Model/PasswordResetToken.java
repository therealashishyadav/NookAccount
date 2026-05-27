package com.Account.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 6-digit OTP code sent to the user's email
    @Column(nullable = false)
    private String otp;

    // Email of the user who requested the reset
    @Column(nullable = false)
    private String email;

    // Token expires 15 minutes after creation
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // Prevents OTP reuse after it has been verified
    @Column(nullable = false)
    private boolean used = false;

    public PasswordResetToken() {}

    public PasswordResetToken(String otp, String email) {
        this.otp       = otp;
        this.email     = email;
        this.expiresAt = LocalDateTime.now().plusMinutes(15);
        this.used      = false;
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}
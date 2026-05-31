package com.Account.Services;

// FILE: src/main/java/com/Account/Services/PasswordResetService.java

import com.Account.Model.PasswordResetToken;
import com.Account.Model.PasswordResetTokenRepository;
import com.Account.Model.User;
import com.Account.Model.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class PasswordResetService {

    @Autowired
    private UserRespository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Reads MAIL_USERNAME from application.properties / Render env vars
    // Must match exactly the Gmail account used for SMTP
    @Value("${spring.mail.username}")
    private String mailFrom;

    // ── STEP 1: User submits email → generate OTP → send email ───────────────
    @Transactional
    public void sendOtp(String email) {

        // Check user exists
        // Silently return if not — don't reveal whether email is registered
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return;
        }

        // Invalidate any existing unused OTP for this email
        tokenRepository.deleteAllByEmail(email);

        // Generate a secure 6-digit OTP
        String otp = generateOtp();

        // Save OTP to database — expires in 15 minutes
        PasswordResetToken resetToken = new PasswordResetToken(otp, email);
        tokenRepository.save(resetToken);

        // Send OTP to user's email
        sendOtpEmail(email, otp, userOpt.get().getFirstName());
    }

    // ── STEP 2: User submits OTP → verify it ─────────────────────────────────
    public boolean verifyOtp(String email, String otp) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByEmailAndUsedFalse(email);

        if (tokenOpt.isEmpty()) return false;

        PasswordResetToken token = tokenOpt.get();

        if (token.isExpired()) return false;
        if (!token.getOtp().equals(otp)) return false;

        return true;
    }

    // ── STEP 3: User submits new password → reset it ──────────────────────────
    @Transactional
    public boolean resetPassword(String email, String otp, String newPassword) {

        // Re-verify OTP before allowing password change
        if (!verifyOtp(email, otp)) return false;

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Clean up — delete all OTPs for this email after successful reset
        tokenRepository.deleteAllByEmail(email);

        return true;
    }

    // ── Helper: generate secure 6-digit OTP ──────────────────────────────────
    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // always 6 digits, never less
        return String.valueOf(otp);
    }

    // ── Helper: send OTP email via Gmail SMTP ────────────────────────────────
    private void sendOtpEmail(String toEmail, String otp, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Password Reset OTP — CribUp");
        message.setText(
            "Hi " + firstName + ",\n\n" +
            "You requested a password reset for your CribUp account.\n\n" +
            "Your OTP is: " + otp + "\n\n" +
            "This OTP is valid for 15 minutes. Do not share it with anyone.\n\n" +
            "If you did not request this, please ignore this email.\n\n" +
            "— The CribUp Team"
        );
        // mailFrom is read from MAIL_USERNAME env var on Render
        // Must match the Gmail account used for SMTP authentication
        message.setFrom(mailFrom);
        mailSender.send(message);
    }
}
package com.Account.Services;

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

    private static final int MAX_OTP_ATTEMPTS = 5;
    private static final int OTP_EXPIRATION_MINUTES = 15;

    @Autowired
    private UserRespository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Transactional
    public void sendOtp(String email) {

        System.out.println("sendOtp called for: " + email);

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            System.out.println("Password reset requested for unknown email: " + email);
            return;
        }

        tokenRepository.deleteAllByEmail(email);

        String otp = generateOtp();

        System.out.println("Generated OTP for password reset: " + email);

        PasswordResetToken resetToken =
                new PasswordResetToken(otp, email);

        resetToken.setAttemptCount(0);

        tokenRepository.save(resetToken);

        System.out.println("Password reset token saved for: " + email);

        sendOtpEmail(
                email,
                otp,
                userOpt.get().getFirstName()
        );

        System.out.println("sendOtp completed for: " + email);
    }

    @Transactional
    public boolean verifyOtp(String email, String otp) {

        Optional<PasswordResetToken> tokenOpt =
                tokenRepository.findByEmailAndUsedFalse(email);

        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken token = tokenOpt.get();

        if (token.isExpired()) {
            tokenRepository.deleteAllByEmail(email);
            return false;
        }

        if (token.getAttemptCount() >= MAX_OTP_ATTEMPTS) {
            tokenRepository.deleteAllByEmail(email);
            return false;
        }

        if (!token.getOtp().equals(otp)) {

            int attempts = token.getAttemptCount() + 1;

            token.setAttemptCount(attempts);

            if (attempts >= MAX_OTP_ATTEMPTS) {
                token.setUsed(true);
            }

            tokenRepository.save(token);

            return false;
        }

        return true;
    }


    @Transactional
    public boolean resetPassword(
            String email,
            String otp,
            String newPassword) {

        Optional<PasswordResetToken> tokenOpt =
                tokenRepository.findByEmailAndUsedFalse(email);

        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken token = tokenOpt.get();

        if (token.isExpired()) {
            tokenRepository.deleteAllByEmail(email);
            return false;
        }

        if (token.getAttemptCount() >= MAX_OTP_ATTEMPTS) {
            tokenRepository.deleteAllByEmail(email);
            return false;
        }

        if (!token.getOtp().equals(otp)) {

            int attempts = token.getAttemptCount() + 1;

            token.setAttemptCount(attempts);

            if (attempts >= MAX_OTP_ATTEMPTS) {
                token.setUsed(true);
            }

            tokenRepository.save(token);

            return false;
        }

        Optional<User> userOpt =
                userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        tokenRepository.deleteAllByEmail(email);

        return true;
    }

    private String generateOtp() {

        SecureRandom random = new SecureRandom();

        int otp = 100000 + random.nextInt(900000);

        return String.valueOf(otp);
    }

    private void sendOtpEmail(
            String toEmail,
            String otp,
            String firstName) {

        try {

            System.out.println(
                    "Attempting to send OTP to: " + toEmail
            );

            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setTo(toEmail);

            message.setSubject(
                    "Your Password Reset OTP — CribUp"
            );

            message.setText(
                    "Hi " + firstName + ",\n\n" +

                    "You requested a password reset for your CribUp account.\n\n" +

                    "Your OTP is: " + otp + "\n\n" +

                    "This OTP is valid for 15 minutes.\n" +

                    "You have a maximum of 5 attempts.\n\n" +

                    "Do not share this OTP with anyone.\n\n" +

                    "If you did not request this password reset, " +
                    "please ignore this email.\n\n" +

                    "— The CribUp Team"
            );

            message.setFrom(mailFrom);

            mailSender.send(message);

            System.out.println(
                    "OTP email sent successfully to: " + toEmail
            );

        } catch (Exception e) {

            System.err.println(
                    "Failed to send OTP email: " +
                    e.getMessage()
            );

            e.printStackTrace();
        }
    }
}
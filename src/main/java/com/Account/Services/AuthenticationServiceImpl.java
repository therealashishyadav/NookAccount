

package com.Account.Services;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Account.DTO.JwtAuthenticationResponse;
import com.Account.DTO.RefreshTokenRequest;
import com.Account.DTO.SigninRequest;
import com.Account.DTO.SignupRequest;
import com.Account.Model.Role;
import com.Account.Model.User;
import com.Account.Model.UserRespository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired private UserRespository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    public User signup(SignupRequest signupRequest) {
        String email = signupRequest.getEmail() != null ? signupRequest.getEmail().trim() : "";
        String firstname = signupRequest.getFirstName() != null ? signupRequest.getFirstName().trim() : "";
        String lastname = signupRequest.getLastName() != null ? signupRequest.getLastName().trim() : "";
        String phone = signupRequest.getPhone() != null ? signupRequest.getPhone().trim() : "";
        String password = signupRequest.getPassword() != null ? signupRequest.getPassword().trim() : "";

        firstname = firstname.replaceAll("\\s{2,}", " ");
        lastname = lastname.replaceAll("\\s{2,}", " ");

        if (email.isEmpty() || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"))
            throw new IllegalArgumentException("Invalid email format");
        if (firstname.isEmpty() || firstname.length() < 2 || !firstname.matches("^[A-Za-z ]+$"))
            throw new IllegalArgumentException("First name must be at least 2 characters and only letters");
        if (lastname.isEmpty() || lastname.length() < 2 || !lastname.matches("^[A-Za-z ]+$"))
            throw new IllegalArgumentException("Last name must be at least 2 characters and only letters");
        if (phone.isEmpty() || phone.length() != 10)
            throw new IllegalArgumentException("Invalid phone number. Must be 10 digits");
        if (password.isEmpty() || password.length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters");
        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("Email already in use");
        if (userRepository.existsByPhone(phone))
            throw new IllegalArgumentException("Phone number already in use");

        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setPhone(phone);
        user.setRole(signupRequest.getRole() != null ? signupRequest.getRole() : Role.USER);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public JwtAuthenticationResponse signin(SigninRequest signinRequest) {
        User user = null;

        if (signinRequest.getEmail() != null && !signinRequest.getEmail().isEmpty()) {
            user = userRepository.findByEmail(signinRequest.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        } else if (signinRequest.getPhone() != null && !signinRequest.getPhone().isEmpty()) {
            user = userRepository.findByPhone(signinRequest.getPhone())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        } else {
            throw new IllegalArgumentException("Email or phone is required");
        }

        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String jwt = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        JwtAuthenticationResponse response = new JwtAuthenticationResponse();
        response.setToken(jwt);
        response.setRefreshToken(refreshToken);
        return response;
    }

    public JwtAuthenticationResponse refereshToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            String userEmail = jwtService.extractUserName(refreshTokenRequest.getToken());
            User user = userRepository.findByEmail(userEmail).orElseThrow();
            if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
                var jwt = jwtService.generateToken(user);
                JwtAuthenticationResponse response = new JwtAuthenticationResponse();
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                return response;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
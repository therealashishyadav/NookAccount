package com.Account.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Account.DTO.GoogleLoginRequest;
import com.Account.DTO.JwtAuthenticationResponse;
import com.Account.DTO.RefreshTokenRequest;
import com.Account.DTO.SigninRequest;
import com.Account.DTO.SignupRequest;
import com.Account.Model.Role;
import com.Account.Model.User;
import com.Account.Model.UserRespository;
import com.Account.Services.AuthenticationService;
import com.Account.Services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

	private final SecurityFilterChain securityFilterChain;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRespository userRespository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	AuthenticationController(SecurityFilterChain securityFilterChain) {
		this.securityFilterChain = securityFilterChain;
	}

	@PostMapping("/signup")
	public ResponseEntity<Map<String, String>> signup(@RequestBody SignupRequest signupRequest) {
		User user = new User();
		user.setFirstName(signupRequest.getFirstName());
		user.setLastName(signupRequest.getLastName());
		user.setEmail(signupRequest.getEmail());
		user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
		user.setPhone(signupRequest.getPhone());
		user.setRole(signupRequest.getRole());
//		return ResponseEntity.ok(Map.of("message", "User registered successfully"));
		   try {
		        authenticationService.signup(signupRequest); 
//				userRespository.save(user);
		        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
		    } catch (IllegalArgumentException e) {
		        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		    }
	}

	@PostMapping("/signin")
	public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SigninRequest signinRequest) {

		return ResponseEntity.ok(authenticationService.signin(signinRequest));
	}

	@PostMapping("/refresh")
	public ResponseEntity<JwtAuthenticationResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		return ResponseEntity.ok(authenticationService.refereshToken(refreshTokenRequest));
	}

	@GetMapping("/role/{email}")
	public ResponseEntity<Role> getRole(@PathVariable String email) {
		Role roles = this.userService.getRole(email);
		return ResponseEntity.ok(roles);
	}
	  @PostMapping("/google")
	    public ResponseEntity<JwtAuthenticationResponse> googleLogin(@RequestBody GoogleLoginRequest request) {
	        JwtAuthenticationResponse response = authenticationService.signinWithGoogle(request.getIdToken());
	        return ResponseEntity.ok(response);
	    }
}

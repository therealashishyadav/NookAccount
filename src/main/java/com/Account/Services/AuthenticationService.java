package com.Account.Services;

import com.Account.DTO.JwtAuthenticationResponse;
import com.Account.DTO.RefreshTokenRequest;
import com.Account.DTO.SigninRequest;
import com.Account.DTO.SignupRequest;
import com.Account.Model.User;

public interface AuthenticationService {

	User signup(SignupRequest signupRequest);
		
	JwtAuthenticationResponse signin(SigninRequest signinRequest);

	public JwtAuthenticationResponse refereshToken(RefreshTokenRequest refreshTokenRequest);

}

package com.Account.Config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Account.Services.JwtService;
import com.Account.Services.UserService;

import io.jsonwebtoken.JwtException;               // ← ADD THIS IMPORT
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserService userService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");

		if (StringUtils.isEmpty(authHeader)
				|| !org.apache.commons.lang3.StringUtils.startsWith(authHeader, "Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		final String jwt = authHeader.substring(7);

		// ─── Wrap JWT parsing in try-catch to avoid 401 on permitAll endpoints ───
		try {
			String userEmail = jwtService.extractUserName(jwt);
			if (StringUtils.isNotEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);

				if (jwtService.isTokenValid(jwt, userDetails)) {
					SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

					UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null,
							userDetails.getAuthorities());

					token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					securityContext.setAuthentication(token);
					SecurityContextHolder.setContext(securityContext);
				}
			}
		} catch (JwtException e) {
			// Log the error (optional) and continue without authentication.
			// This prevents expired/malformed tokens from blocking permitAll endpoints.
			logger.debug("Invalid JWT token: " + e.getMessage());
		}

		filterChain.doFilter(request, response);
	}
}
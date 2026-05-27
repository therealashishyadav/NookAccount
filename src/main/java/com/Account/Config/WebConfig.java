package com.Account.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
	    registry.addMapping("/**")
	            .allowedOrigins(
//	                "http://localhost:4200",
//	                "https://nookly-frontend-hslj.vercel.app",
	                "https://cribup.vercel.app"
//	                "https://apigateway-x0ku.onrender.com",
//	                "https://nookly-git-main-therealashishyadav1.vercel.app",
//	                "https://nookly-4kojk0tjf-therealashishyadav1.vercel.app"
	            )
	            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
	            .allowedHeaders("*")
	            .allowCredentials(true);
	}
}
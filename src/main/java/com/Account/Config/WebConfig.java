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
	                "http://localhost:4200",
	                "https://nookly-frontend-hslj.vercel.app",   // ADD THIS
	                "https://apigateway-x0ku.onrender.com"        // ADD THIS
	            )
	            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
	            .allowedHeaders("*")
	            .allowCredentials(true);
	}
}
package com.Account.Controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
	

	public AdminController() {
		super();
	}

	@GetMapping
	public ResponseEntity<String> sayHello(){
		return ResponseEntity.ok("Admin LoggedIn");
	}

	@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> helloAdmin(){
        return ResponseEntity.ok("Hello Admin");
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public ResponseEntity<String> helloUser(){
        return ResponseEntity.ok("Hello User");
    }
	
    @PreAuthorize("hasRole('MANAGEMENT')")
    @GetMapping("/management")
    public ResponseEntity<String> helloManagement(){
        return ResponseEntity.ok("Hello Management");
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer")
    public ResponseEntity<String> helloCustomer(){
        return ResponseEntity.ok("Hello Customer");
    }
    
}

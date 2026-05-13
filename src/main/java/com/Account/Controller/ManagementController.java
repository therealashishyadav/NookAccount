package com.Account.Controller;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/api/v1/management")
public class ManagementController {

	public ManagementController() {
		super();
	}
	
	@GetMapping
	public ResponseEntity<String> sayHello(){
		return ResponseEntity.ok("Management Loggedin");
	}
	
	
}

package com.Account.Controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Account.Model.User;
import com.Account.Services.UserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
	
	@Autowired
	private UserService userService;

	@GetMapping
	public ResponseEntity<Map<String, String>> sayHello() {
		return ResponseEntity.ok(Map.of("message", "User LoggedIn"));

	}

	@GetMapping("/all")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> user = userService.getAllUsers();
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@PutMapping("/{id}/activate")
	public ResponseEntity<User> activateUser(@PathVariable long id) {
		User user = userService.activateUser(id);
		return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
	}

	@PutMapping("/{id}/deactivate")
	public ResponseEntity<User> deactivateUser(@PathVariable long id) {
		User user = userService.deactivateUser(id);
		return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable long id) {
		User user = userService.getUserById(id);		
		if(user != null) {
			return new ResponseEntity<>(user, HttpStatus.OK);
		}else{
			return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
		}
	}	
	
	
	@PutMapping("/")
	public ResponseEntity<User> updateUser(@PathVariable long id, @RequestBody User updateUser){
		User user = userService.updateUser(id, updateUser);
		if(user != null) {
			return new ResponseEntity<>(user, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping("/")
	public ResponseEntity<Void> deleteUser(@PathVariable long id){
		boolean isDeleted = userService.deteleUser(id);
		if(isDeleted){
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}	
}

package com.Account.DTO;

import com.Account.Model.Role;

import lombok.Data;

@Data

public class SignupRequest {

	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String phone;
	private String confirm_password;
	private Role role;
	
	
	
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	
	
	public SignupRequest(String firstName, String lastName, String email, String password, String phone,
			String confirm_password, Role role) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.confirm_password = confirm_password;
		this.role = role;
	}
	public String getConfirm_password() {
		return confirm_password;
	}
	public void setConfirm_password(String confirm_password) {
		this.confirm_password = confirm_password;
	}
	public SignupRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
}

package com.Account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.Account.Model.Role;
import com.Account.Model.User;
import com.Account.Model.UserRespository;

@SpringBootApplication
@EnableDiscoveryClient
public class AccountApplication implements CommandLineRunner {

	@Autowired
	private UserRespository userRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(AccountApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		User adminAccount = userRepository.findByRole(Role.ADMIN);
		
		if(null == adminAccount) {
			User user = new User();
			
			user.setEmail("admin");
			user.setFirstName("admin");
			user.setLastName("admin");
			user.setRole(Role.ADMIN);
			user.setPassword(new BCryptPasswordEncoder().encode("pass"));
			userRepository.save(user);
		}

	}
	
	

}

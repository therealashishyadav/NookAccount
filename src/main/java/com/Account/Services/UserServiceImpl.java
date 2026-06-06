package com.Account.Services;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Account.Model.Role;
import com.Account.Model.User;
import com.Account.Model.UserRespository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRespository userRepository;

	@Override
	public final UserDetailsService userDetailsService() {
		return new UserDetailsService() {
			
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
			}
		};
	}

	@Override
	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		return userRepository.findAll();
	}

	@Override
	public User getUserById(long id) {
		// TODO Auto-generated method stub
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public User updateUser(long id, User updateUser) {
		// TODO Auto-generated method stub
		return userRepository.findById(id).map( user ->{
			user.setFirstName(updateUser.getFirstName());
			user.setLastName(updateUser.getLastName());
			user.setEmail(updateUser.getEmail());
			user.setPhone(updateUser.getPhone());
			return userRepository.save(user);
		}).orElse(null);
	}

	@Override
	public User activateUser(long id) {
		return userRepository.findById(id).map(user -> {
			user.setActive(true);
			return userRepository.save(user);
		}).orElse(null);
	}

	@Override
	public User deactivateUser(long id) {
		return userRepository.findById(id).map(user -> {
			user.setActive(false);
			return userRepository.save(user);
		}).orElse(null);
	}

	@Override
	public boolean deteleUser(long id) {
		// TODO Auto-generated method stub
		return userRepository.findById(id).map( user ->{
			userRepository.delete(user);
			return true;
		}).orElse(false);
	}

	@Override
	public Role getRole(String email) {
		return this.userRepository.findByEmail(email).map(User::getRole)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	@Override
	public User getOwnerByEmail(String email) {
		  return userRepository.findByEmail(email)
	                .orElseThrow(() -> new RuntimeException("Owner not found with email: " + email));
	    }

//	@Override
//	 public Role getRole(String email) {
//        return userRepository.findByEmail(email)
//                .map(User::getRole)
//                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
//    }

	
	
}

package com.Account.Services;


import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.Account.Model.Role;
import com.Account.Model.User;

public interface UserService {

	UserDetailsService userDetailsService();

	List<User> getAllUsers();

	User getUserById(long id);

	User updateUser(long id, User updateUser);

	User activateUser(long id);

	User deactivateUser(long id);

	boolean deteleUser(long id);

	Role getRole(String email);

    User getOwnerByEmail(String email);

}

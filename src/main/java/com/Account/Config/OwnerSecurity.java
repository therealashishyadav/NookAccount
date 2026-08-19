package com.Account.Config;

import com.Account.Model.User;
import com.Account.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("ownerSecurity")
public class OwnerSecurity {

    @Autowired
    private UserService userService;

    public boolean isSelf(long id, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return false;
        }
        String callerEmail = authentication.getName();
        User target = userService.getUserById(id);
        return target != null && target.getEmail() != null
                && target.getEmail().equalsIgnoreCase(callerEmail);
    }
}
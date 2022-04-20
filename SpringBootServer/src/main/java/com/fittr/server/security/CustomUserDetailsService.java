package com.fittr.server.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//import com.appserver.apis.users.UserRepo;
//import com.appserver.apis.users.Users;

import com.fittr.server.repository.UserRepo;
import com.fittr.server.model.Users;

/**
 * This class is developed to implement basic web security
 *
 * @author Rakesh Kumar
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<Users> user = userRepo.findByEmail(userName);
        if (user == null)
            throw new UsernameNotFoundException(userName);
        return user.map(CustomUserDetails::new).get();
    }

}

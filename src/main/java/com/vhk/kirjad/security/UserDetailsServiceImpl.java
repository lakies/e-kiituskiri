package com.vhk.kirjad.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!"kasutaja".equals(username)) {
            throw new UsernameNotFoundException(username);
        }

        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setPassword("$2a$10$hVX5IRvbRx.SuTUqK3T9r.BZUu7X8NfkuU9XpGwyOWTh5AyjhE/jO");
        applicationUser.setUsername("kasutaja");

        return new User(applicationUser.getUsername(), applicationUser.getPassword(), emptyList());
    }
}
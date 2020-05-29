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
        applicationUser.setPassword("$2a$10$h/EBPgsWk91.u0s6e7ewRusJ8.u.uj/SFdJGrixFu1N/oa.R3BkRq");
        applicationUser.setUsername("kasutaja");

        return new User(applicationUser.getUsername(), applicationUser.getPassword(), emptyList());
    }
}
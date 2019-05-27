package com.dyptan.service;

import com.dyptan.model.User;
import com.dyptan.model.UserAuthDetails;
import com.dyptan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserAuthDetailsService implements UserDetailsService {

    Logger log = Logger.getLogger(this.getClass().getName());

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUsers = userRepository.findByUsername(username);

        log.info("User was found::" + optionalUsers);

        optionalUsers
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return optionalUsers
                .map(UserAuthDetails::new).get();
    }
}
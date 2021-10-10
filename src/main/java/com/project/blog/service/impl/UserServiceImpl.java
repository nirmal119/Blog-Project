package com.project.blog.service.impl;

import com.project.blog.entity.User;
import com.project.blog.principal.UserPrincipal;
import com.project.blog.repository.UserRepository;
import com.project.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
 public class UserServiceImpl implements UserService {

    @Autowired
    private  UserRepository userRepository;

    @Override
    public Optional<User> findByName(String username) {
        return userRepository.findByName(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.saveAndFlush(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = findByName(username);

        if(userOptional.isEmpty()){
            throw new UsernameNotFoundException("User not found!");
        }
        User user = userOptional.get();
        return new UserPrincipal(user);
    }
}
package com.project.blog.service;

import com.project.blog.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);
    User save(User user);
}
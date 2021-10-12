package com.project.blog.controller;

import com.project.blog.entity.User;
import com.project.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegistrationController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public User registration() {

        return new User();
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public void createNewUser(@RequestBody User user,
                                BindingResult bindingResult) {
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        if (userService.findByName(user.getName()).isPresent()) {
            bindingResult
                    .rejectValue("name", "error.user",
                            "There is already a user registered with the Name provided");
        }

        if (!bindingResult.hasErrors()) {
            user.setRole("ROLE_USER");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.save(user);
        }
    }
}
package com.project.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class LoginController {
    @RequestMapping("/login")
    public String loginPage(){
        return "login";
    }
    @RequestMapping("/logoutSuccess")
    public String logout(){
        return "logoutSuccess";
    }
}
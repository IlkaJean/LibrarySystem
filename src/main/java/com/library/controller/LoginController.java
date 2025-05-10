package com.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping({"/", "/login"})
    public String showLoginForm() {
        return "login";  // Thymeleaf will resolve to templates/login.html
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";  // If you also have templates/register.html
    }
}

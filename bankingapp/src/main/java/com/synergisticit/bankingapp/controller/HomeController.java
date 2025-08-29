package com.synergisticit.bankingapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // New login page path
    @GetMapping("/auth/login")
    public String loginPage() {
        return "login";              // /WEB-INF/views/login.jsp
    }

    @GetMapping("/home")
    public String homeGet() {
        return "home";               // /WEB-INF/views/home.jsp
    }

    // Optional: "/" redirects to /home (unauth users will be sent to login by Security)
    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    // Public access-denied page
    @GetMapping("/403")
    public String accessDenied() {
        return "403";
    }
}

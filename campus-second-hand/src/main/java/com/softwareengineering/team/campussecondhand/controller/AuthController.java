package com.softwareengineering.team.campussecondhand.controller;

import com.softwareengineering.team.campussecondhand.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final UserService userService;
    public AuthController(UserService userService){ this.userService = userService; }

    @GetMapping("/login")
    public String loginPage(){ return "login"; }

    @GetMapping("/register")
    public String registerPage(){ return "register"; }

    @PostMapping("/register")
    public String register(@RequestParam String phone,
                           @RequestParam String password,
                           @RequestParam(required = false) String username,
                           Model model){
        try{
            userService.register(phone, password, username);
            return "redirect:/login";
        } catch (Exception e){
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}

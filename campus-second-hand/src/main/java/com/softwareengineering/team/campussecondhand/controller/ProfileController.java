package com.softwareengineering.team.campussecondhand.controller;

import com.softwareengineering.team.campussecondhand.entity.Product;
import com.softwareengineering.team.campussecondhand.entity.User;
import com.softwareengineering.team.campussecondhand.service.ProductService;
import com.softwareengineering.team.campussecondhand.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;
    private final ProductService productService;
    
    public ProfileController(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }
    
    @GetMapping
    public String profile(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByPhone(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        
        // 加载用户发布的商品
        List<Product> products = productService.findByUserId(user.getId());
        
        model.addAttribute("user", user);
        model.addAttribute("products", products);
        
        return "profile";
    }
    
    @PostMapping("/update")
    public String updateProfile(
            Authentication authentication,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String sno,
            @RequestParam(required = false) String dormitory,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByPhone(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        
        // 更新用户信息
        if (username != null && !username.isBlank()) {
            user.setUsername(username);
        }
        if (realName != null) {
            user.setRealName(realName);
        }
        if (sno != null) {
            user.setSno(sno);
        }
        if (dormitory != null) {
            user.setDormitory(dormitory);
        }
        
        userService.updateUser(user);
        
        redirectAttributes.addFlashAttribute("message", "个人信息更新成功");
        return "redirect:/profile";
    }
}
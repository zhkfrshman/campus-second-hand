package com.softwareengineering.team.campussecondhand.controller;

import com.softwareengineering.team.campussecondhand.entity.CartItem;
import com.softwareengineering.team.campussecondhand.entity.Product;
import com.softwareengineering.team.campussecondhand.entity.User;
import com.softwareengineering.team.campussecondhand.service.CartService;
import com.softwareengineering.team.campussecondhand.service.ProductService;
import com.softwareengineering.team.campussecondhand.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;

    public ProfileController(UserService userService, ProductService productService, CartService cartService) {
        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping
    public String showProfile(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByPhone(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        
        // 使用正确的方法获取用户发布的商品
        List<Product> myProducts = productService.findByUserId(user.getId());
        
        // 使用现有的CartService方法获取购物车项
        List<CartItem> cartItems = cartService.getCartItems(user.getId());
        
        model.addAttribute("user", user);
        model.addAttribute("myProducts", myProducts);
        model.addAttribute("cartItems", cartItems);
        
        return "profile";
    }

    @GetMapping("/edit")
    public String showEditForm(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByPhone(authentication.getName());
        model.addAttribute("user", user);
        
        return "profile-edit";
    }

    @PostMapping("/edit")
    public String updateProfile(
            Authentication authentication,
            @RequestParam String username,
            @RequestParam(required = false) String sno,
            @RequestParam(required = false) String dormitory,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByPhone(authentication.getName());
        user.setUsername(username);
        user.setSno(sno);
        user.setDormitory(dormitory);
        
        userService.updateUser(user);
        
        redirectAttributes.addFlashAttribute("message", "个人信息已更新");
        return "redirect:/profile";
    }
}
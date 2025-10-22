package com.softwareengineering.team.campussecondhand.controller;

import com.softwareengineering.team.campussecondhand.entity.*;
import com.softwareengineering.team.campussecondhand.service.CartService;
import com.softwareengineering.team.campussecondhand.service.ProductService;
import com.softwareengineering.team.campussecondhand.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;
    
    public OrderController(CartService cartService, 
                          ProductService productService,
                          UserService userService) {
        this.cartService = cartService;
        this.productService = productService;
        this.userService = userService;
    }
    
    @GetMapping("/checkout")
    public String checkout(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByPhone(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        
        // 获取购物车项目
        List<CartItem> cartItems = cartService.getCartItems(user.getId());
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        
        BigDecimal total = BigDecimal.ZERO;
        Map<Long, Product> productMap = new HashMap<>();
        
        // 获取商品信息和计算总价
        for (CartItem item : cartItems) {
            Product product = productService.findById(item.getSid());
            if (product != null) {
                productMap.put(item.getSid(), product);
                BigDecimal itemTotal = BigDecimal.valueOf(product.getPrice())
                                               .multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(itemTotal);
            }
        }
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("productMap", productMap);
        model.addAttribute("total", total);
        model.addAttribute("user", user);
        
        return "order-checkout";
    }
    
    @PostMapping("/submit")
    public String submitOrder(
            Authentication authentication,
            @RequestParam String contactName,
            @RequestParam String address,
            @RequestParam String phone,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByPhone(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        
        // 获取购物车项目
        List<CartItem> cartItems = cartService.getCartItems(user.getId());
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        
        // 在此处理订单提交逻辑
        // 可以创建订单实体、减少库存、清空购物车等操作
        
        redirectAttributes.addFlashAttribute("message", "订单提交成功！");
        return "redirect:/product/list";
    }
}
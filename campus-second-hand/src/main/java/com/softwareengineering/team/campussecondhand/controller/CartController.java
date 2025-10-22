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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
// 添加这个导入语句
import java.util.Map;
// 同时确保也导入了HashMap
import java.util.HashMap;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;

    public CartController(CartService cartService, ProductService productService, UserService userService){
        this.cartService = cartService;
        this.productService = productService;
        this.userService = userService;
    }

    @PostMapping("/add")
    @ResponseBody
    public String addToCartAjax(
        @RequestParam(required = false) Long uid, 
        @RequestParam Long sid, 
        @RequestParam(defaultValue = "1") Integer qty,
        Authentication authentication) {
        try {
            // 如果没有传入uid，则使用当前登录用户
            if (uid == null && authentication != null) {
                User currentUser = userService.findByPhone(authentication.getName());
                if (currentUser != null) {
                    uid = currentUser.getId();
                }
            }
            
            if (uid == null) {
                return "请先登录";
            }
            
            // 检查商品是否存在并有足够库存
            Product product = productService.findById(sid);
            if (product == null) {
                return "商品不存在";
            }
            
            if (product.getCount() < qty) {
                return "商品库存不足，当前库存: " + product.getCount();
            }
            
            // 检查购物车中已有的数量
            List<CartItem> existingItems = cartService.getCartItems(uid);
            int existingQty = 0;
            for (CartItem item : existingItems) {
                if (item.getSid().equals(sid)) {
                    existingQty = item.getQuantity();
                    break;
                }
            }
            
            // 检查总数量是否超过库存
            if (existingQty + qty > product.getCount()) {
                return "商品库存不足，您的购物车中已有" + existingQty + "件，当前库存: " + product.getCount();
            }
            
            cartService.addToCart(uid, sid, qty);
            return "OK";
        } catch(Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public String updateQuantity(@PathVariable Long id, @RequestParam int qty, Authentication authentication) {
        try {
            if (authentication == null) {
                return "error: not authenticated";
            }
            
            User user = userService.findByPhone(authentication.getName());
            if (user == null) {
                return "error: user not found";
            }
            
            // 获取当前购物车项
            CartItem item = cartService.getCartItems(user.getId()).stream()
                .filter(ci -> ci.getId().equals(id))
                .findFirst()
                .orElse(null);
                
            if (item == null) {
                return "error: item not found";
            }
            
            // 检查库存
            Product product = productService.findById(item.getSid());
            if (product == null) {
                return "error: product not found";
            }
            
            if (product.getCount() < qty) {
                return "error: 商品库存不足，当前库存: " + product.getCount();
            }
            
            // 移除旧项
            cartService.removeItem(id);
            // 添加新的数量
            cartService.addToCart(item.getUid(), item.getSid(), qty);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 使用Authentication提供当前用户
    @GetMapping
    public String viewCart(Authentication authentication, Model model){
        if (authentication == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByPhone(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        
        // 使用现有的getCartItems方法
        List<CartItem> items = cartService.getCartItems(user.getId());
        
        // 计算总金额并预加载商品信息
        BigDecimal total = BigDecimal.ZERO;
        Map<Long, Product> productMap = new HashMap<>();
        
        // 为每个购物车项关联商品信息
        for (CartItem item : items) {
            Product product = productService.findById(item.getSid());
            if (product != null) {
                productMap.put(item.getSid(), product);
                if (product.getPrice() != null) {
                    // 计算小计并累加到总金额
                    BigDecimal itemTotal = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity()));
                    total = total.add(itemTotal);
                }
            }
        }
        
        model.addAttribute("cartItems", items);
        model.addAttribute("productMap", productMap);
        model.addAttribute("total", total);
        
        return "cart";
    }

    // 保留原来的方法，但增加重定向到新方法
    @GetMapping("/view")
    public String view(@RequestParam Long uid, Model model){
        List<CartItem> items = cartService.getCartItems(uid);
        // fetch products for each item
        List<Product> prods = new ArrayList<>();
        for (CartItem ci : items) {
            Product p = productService.findById(ci.getSid());
            prods.add(p);
        }
        model.addAttribute("items", items);
        model.addAttribute("products", prods);
        return "cart";
    }
    
    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartService.removeItem(id);
        return "redirect:/cart";
    }
}
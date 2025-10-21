package com.softwareengineering.team.campussecondhand.controller;

import com.softwareengineering.team.campussecondhand.entity.CartItem;
import com.softwareengineering.team.campussecondhand.service.CartService;
import com.softwareengineering.team.campussecondhand.service.ProductService;
import com.softwareengineering.team.campussecondhand.entity.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final ProductService productService;

    public CartController(CartService cartService, ProductService productService){
        this.cartService = cartService;
        this.productService = productService;
    }

    @PostMapping("/add")
    @ResponseBody
    public String addToCartAjax(@RequestParam Long uid, @RequestParam Long sid, @RequestParam(defaultValue = "1") Integer qty){
        try{
            cartService.addToCart(uid, sid, qty);
            return "OK";
        } catch(Exception e){
            return "ERROR: " + e.getMessage();
        }
    }

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
}

package com.softwareengineering.team.campussecondhand.controller;

import com.softwareengineering.team.campussecondhand.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final ProductService productService;
    public HomeController(ProductService productService){ this.productService = productService; }

    @GetMapping({"/", "/index"})
    public String index(Model model){
        var page = productService.listAvailable(0, 12);
        model.addAttribute("products", page.getContent());
        return "index";
    }
}

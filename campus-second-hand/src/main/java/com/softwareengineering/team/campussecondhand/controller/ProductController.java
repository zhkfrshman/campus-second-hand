package com.softwareengineering.team.campussecondhand.controller;

import com.softwareengineering.team.campussecondhand.entity.Product;
import com.softwareengineering.team.campussecondhand.service.MessageService;
import com.softwareengineering.team.campussecondhand.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
@Controller
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final MessageService messageService;

    public ProductController(ProductService productService, MessageService messageService) {
        this.productService = productService;
        this.messageService = messageService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "12") int size,
                       @RequestParam(required = false) String q,
                       Model m) {
        Page<Product> pg;
        if (q == null || q.isBlank()) {
            pg = productService.listAvailable(page, size);
        } else {
            // fallback: use name search via repository method (we didn't define interface method with q param earlier,
            // but productService could be extended; for now we show all)
            pg = productService.listAvailable(page, size);
        }
        m.addAttribute("products", pg.getContent());
        m.addAttribute("page", pg);
        return "product-list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model m) {
        var p = productService.findById(id);
        if (p == null) return "redirect:/product/list";
        m.addAttribute("product", p);
        m.addAttribute("messages", messageService.findBySid(id));
        return "product-detail";
    }

    @GetMapping("/publish")
    public String publishPage() { return "publish"; }

    @PostMapping("/publish")
    public String publish(@RequestParam String name,
                          @RequestParam Double price,
                          @RequestParam(required = false) String remark,
                          @RequestParam(required = false) MultipartFile imageFile,
                          @RequestParam(required = false) Long uid // in dev: you can pass uid=1 or get from session
    ) {
        try {
            productService.createProduct(name, price, remark, imageFile, uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/product/list";
    }
}

package com.softwareengineering.team.campussecondhand.controller;

import com.softwareengineering.team.campussecondhand.entity.Message;
import com.softwareengineering.team.campussecondhand.entity.Product;
import com.softwareengineering.team.campussecondhand.entity.User;
import com.softwareengineering.team.campussecondhand.service.MessageService;
import com.softwareengineering.team.campussecondhand.service.ProductService;
import com.softwareengineering.team.campussecondhand.service.UserService;
import com.softwareengineering.team.campussecondhand.util.FileStorageUtil;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final UserService userService;
    private final FileStorageUtil fileStorageUtil;
    private final MessageService messageService;

    public ProductController(ProductService productService, UserService userService, 
                            FileStorageUtil fileStorageUtil, MessageService messageService) {
        this.productService = productService;
        this.userService = userService;
        this.fileStorageUtil = fileStorageUtil;
        this.messageService = messageService;
    }

    // 确保这个方法在 @GetMapping("/{id}") 之前定义
    @GetMapping("/add")
    public String showAddProductForm() {
        return "product-add";
    }

    // 修改添加商品的方法，添加日志记录

    @PostMapping("/add")
    public String addProduct(
            Authentication authentication,
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam(defaultValue = "1") Integer count,  // 添加数量参数
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) MultipartFile image,
            RedirectAttributes redirectAttributes) {

        // 获取当前登录用户
        User currentUser = userService.findByPhone(authentication.getName());
        if (currentUser == null) {
            return "redirect:/login";
        }

        // 处理图片上传
        String imagePath = null;
        if (image != null && !image.isEmpty()) {
            imagePath = fileStorageUtil.storeFile(image);
            System.out.println("上传图片路径: " + imagePath);
        }

        try {
            // 创建商品
            Product product = new Product();
            product.setName(name);
            product.setPrice(price.doubleValue());
            product.setRemark(remark);
            product.setImage(imagePath);
            product.setUid(currentUser.getId());
            product.setCount(count);  // 设置商品数量
            product.setDisplay(1);    // 使用整数1代替boolean true

            Product savedProduct = productService.saveProduct(product);
            System.out.println("保存后的商品图片路径: " + savedProduct.getImage());
            redirectAttributes.addFlashAttribute("message", "商品发布成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "商品发布失败：" + e.getMessage());
        }
        return "redirect:/product/list";
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
            // fallback: use name search via repository method
            pg = productService.listAvailable(page, size);
        }
        m.addAttribute("products", pg.getContent());
        m.addAttribute("page", pg);
        return "product-list";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model m) {
        var product = productService.findById(id);
        if (product == null) {
            return "redirect:/product/list";
        }
        
        // 加载卖家信息
        User seller = null;
        if (product.getUid() != null) {
            seller = userService.findById(product.getUid());
        }
        
        // 加载留言
        List<Message> messages = messageService.findBySid(id);
        
        m.addAttribute("product", product);
        m.addAttribute("seller", seller);
        m.addAttribute("messages", messages);
        
        return "product-detail";
    }

    // @GetMapping("/publish")
    // public String publishPage() { return "publish"; }

    // @PostMapping("/publish")
    // public String publish(@RequestParam String name,
    //                       @RequestParam Double price,
    //                       @RequestParam(required = false) String remark,
    //                       @RequestParam(required = false) MultipartFile imageFile,
    //                       @RequestParam(required = false) Long uid,
    //                       RedirectAttributes redirectAttributes) {
    //     try {
    //         productService.createProduct(name, price, remark, imageFile, uid);
    //         redirectAttributes.addFlashAttribute("message", "商品发布成功！");
    //     } catch (Exception e) {
    //         redirectAttributes.addFlashAttribute("error", "商品发布失败：" + e.getMessage());
    //         e.printStackTrace();
    //     }
    //     return "redirect:/product/list";
    // }
}